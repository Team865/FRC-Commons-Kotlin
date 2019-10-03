package ca.warp7.frc.coroutines

import ca.warp7.frc.action.Action
import ca.warp7.frc.coroutines.CycleState.*
//import edu.wpi.first.wpilibj.Notifier
//import edu.wpi.first.wpilibj.RobotBase
//import edu.wpi.first.wpilibj.Timer
import java.lang.StringBuilder
import kotlin.coroutines.*


@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalCoroutineAction
open class CoroutineAction: Action {


    override fun shouldFinish(): Boolean {
        if (cycleState == Periodic && cycleCount == 0) {
            println("$this is not finishing; no override implementation")
        }
        return false
    }

    override fun update() {
        if (cycleState == Periodic && cycleCount == 0) {
            println("$this is updating; no override implementation")
        }
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
            val coroutine = CoroutineWithContinuation(coroutineHandle, this@CoroutineAction, debug)
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

    internal fun stop0() {
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

