package ca.warp7.frc.coroutines

import ca.warp7.frc.coroutines.CycleState.*
//import edu.wpi.first.wpilibj.Notifier
//import edu.wpi.first.wpilibj.RobotBase
//import edu.wpi.first.wpilibj.Timer
import java.lang.StringBuilder
import kotlin.coroutines.*

/**
 * An [Action] defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the [firstCycle],
 * [update], [lastCycle], [interrupt], and [shouldFinish] methods.
 *
 * [Action] is subclassed to provide custom functionality. However, if multiple actions need
 * to be scheduled, the coroutine functionality as provided by the [ActionCoroutine] interface
 * through the [run] method
 *
 * @since 4.0
 */

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalActionDSL
open class Action {

    /**
     * Returns whether or not the code has finished execution.
     *
     * Contract: [shouldFinish] is called only after [firstCycle] is called
     *
     * @since 5.0
     */
    open fun shouldFinish(): Boolean {
        if (cycleState == Periodic && cycleCount == 0) {
            warnThis("is not finishing; no override implementation")
        }
        return false
    }

    /**
     * Returns whether or not the code has finished execution.
     */
    @Deprecated("", ReplaceWith("shouldFinish()"))
    open val shouldFinish: Boolean
        get() = shouldFinish()

    /**
     * Run code once when the action is started
     *
     * Contract: [firstCycle] is called whenever this action is scheduled
     * before any other method, including [shouldFinish]. [setEpoch] is
     * called once immediately before this method is called to track time
     *
     * @since 5.0
     */
    open fun firstCycle() {
        warnThis("is started (first cycle); no override implementation")
    }

    /**
     * Run code once when the action is started, usually for set up.
     * This method is called first before shouldFinish
     */
    @Deprecated("", ReplaceWith("firstCycle()"))
    open fun start() {
        firstCycle()
    }

    /**
     * Periodically updates the action
     */
    open fun update() {
        if (cycleState == Periodic && cycleCount == 0) {
            warnThis("is updating; no override implementation")
        }
    }

    /**
     * Run the last cycle
     *
     * Contract: [lastCycle] is called if and only if [shouldFinish] has returned
     * true in the current cycle and [firstCycle] is called
     */
    open fun lastCycle() {
        warnThis("is stopped (last cycle); no override implementation")
    }

    /**
     * Run code once when the action finishes, usually for clean up
     */
    @Deprecated("", ReplaceWith(""))
    open fun stop(interrupted: Boolean) {
        if (interrupted) {
            interrupt()
        } else {
            lastCycle()
        }
    }

    /**
     * Interrupt the action
     *
     * Contract: [interrupt] is called any time the action control manager
     * determines that the action should stop regardless of [shouldFinish]
     * However, it will only be called after [firstCycle]. [interrupt] and
     * [lastCycle] are exclusive, meaning that they won't be both called in
     * the same action cycle. [interrupt] will be used for ending timeout
     * actions
     */
    open fun interrupt() {
        warnThis("is interrupted; no override implementation")
    }

    /**
     * Sends a warning with [msg]
     *
     * This can be subclassed for custom-route warning messages
     */
    open fun warnThis(msg: String) {
        println("ERROR $this $msg")
    }

    /**
     * Gets the current time in seconds
     */
    open fun time(): Double {
        return System.nanoTime() / 1E9
    }

    private val coroutines: MutableList<CoroutineWithContinuation> = mutableListOf()

    private var cycleState = FirstCycle

    private var coroutineCycleCompleted = false

    private var epoch = 0.0

    var cycleCount = 0
        private set

    protected var name: String = javaClass.simpleName

    override fun toString(): String {
        if (coroutines.isEmpty()) {
            return name
        }
        val builder = StringBuilder()
        builder.append(name).append("[")
        coroutines.forEachIndexed { index, coroutine ->
            builder.append(index).append(",")
            builder.append(coroutine)
        }
        builder.append("]")
        return builder.toString()
    }

    internal fun Routine.run() {
        if (cycleState == Periodic || cycleState == FirstCycle) {
            val coroutine = CoroutineWithContinuation(coroutineHandle, this@Action, debug)
            coroutineHandle++
            coroutine.nextStep = block.createCoroutine(coroutine, coroutine)
            coroutines.add(coroutine)
        }
    }

    protected fun runFinally(block: () -> Unit) {
        if (cycleState == Periodic) {
            runCoroutineCycle()
            block()
        }
    }

    protected fun setEpoch() {
        epoch = time()
    }

    private fun runCoroutineCycle() {
        if (!coroutineCycleCompleted) {
            var doneState = false
            for (coroutine in coroutines) {
                if (coroutine.advanceStateIsDone()) {
                    doneState = true
                }
            }
            if (doneState) {
                coroutines.removeAll { it.state == CoroutineState.Done }
            }
            coroutineCycleCompleted = true
        }
    }

    internal fun advanceState() {
        when (cycleState) {
            FirstCycle -> {
                setEpoch()
                firstCycle()
                cycleState = Periodic
                cycleCount = 0
            }
            Periodic -> {
                if (shouldFinish()) {
                    lastCycle()
                    cycleState = Done
                } else {
                    coroutineCycleCompleted = false
                    update()
                    runCoroutineCycle()
                    cycleCount++
                }
            }
            Done -> {
                warnThis("is already done; cannot advance state")
                cycleState = Idle
            }
            Idle -> Unit
        }
    }

    internal fun stop() {
        when (cycleState) {
            FirstCycle -> Unit
            Periodic -> {
                interrupt()
                cycleState = Done
            }
            Done -> {
                warnThis("is already done; cannot stop it again")
            }
            Idle -> Unit
        }
    }

    companion object {
        var coroutineHandle = 0
    }
}

