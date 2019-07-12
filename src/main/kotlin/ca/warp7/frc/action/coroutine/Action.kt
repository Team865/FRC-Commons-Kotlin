package ca.warp7.frc.action.coroutine

import ca.warp7.frc.action.coroutine.CycleState.*
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.Timer
import kotlin.coroutines.*

/**
 * An [Action] defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the [firstCycle],
 * [update], [lastCycle], [interrupt], and [shouldFinish] methods.
 *
 * [Action] is subclassed to provide custom functionality. However, if multiple actions need
 * to be scheduled, the coroutine functionality as provided by the [ActionCoroutine] interface
 * through the [run] and [dispatch] methods
 */

@Suppress("MemberVisibilityCanBePrivate")
open class Action {

    /**
     * Returns whether or not the code has finished execution.
     *
     * Contract: [shouldFinish] is called only after [firstCycle] is called
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
        return Timer.getFPGATimestamp()
    }

    private val coroutines: MutableList<Dispatch> = mutableListOf()

    private var cycleState = FirstCycle

    private var coroutineCycleCompleted = false

    private var epoch = 0.0

    private var cycleCount = 0

    protected var name: String = javaClass.simpleName

    override fun toString(): String {
        if (coroutines.isEmpty()) {
            return name
        }
        return name + coroutines.joinToString(", ", "[", "]")
    }

    protected fun Routine.run() {
        dispatch(debug, block)
    }

    protected fun dispatch(debug: Boolean = false,
                           block: suspend ActionCoroutine.() -> Unit) {
        if (cycleState == Periodic || cycleState == FirstCycle) {
            val coroutine = Dispatch(debug)
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
}

class SynchronizedControl {
    private var currentAction = Action()

    fun setAction(action: Action) {
        synchronized(this) {
            currentAction = action
            currentAction.advanceState()
        }
    }

    fun interrupt() {
        synchronized(this) {
            currentAction.stop()
        }
    }

    fun updateActions() {
        synchronized(this) {
            currentAction.advanceState()
        }
    }
}

private enum class CoroutineState {
    Ready, Done
}

private enum class CycleState {
    FirstCycle, Periodic, Done, Idle
}

/**
 * [ActionDSL] marks a method to be part of the Action library
 */

@DslMarker
annotation class ActionDSL

@ActionDSL
interface ActionCoroutine {

    @ActionDSL
    suspend operator fun <T : Action> T.unaryPlus()

    @ActionDSL
    suspend fun cancel()

    @ActionDSL
    suspend fun delay(seconds: Number)

    @ActionDSL
    suspend fun lock()

    @ActionDSL
    suspend fun free()

    @ActionDSL
    suspend fun skip()

    @ActionDSL
    suspend fun parallel(block: suspend ActionCoroutine.() -> Unit)
}

private class Dispatch(val debug: Boolean = false) : ActionCoroutine, Continuation<Unit> {

    var nextStep: Continuation<Unit>? = null

    var locked = false

    var state = CoroutineState.Ready

    val subActions: MutableList<Action> = mutableListOf()

    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext


    fun advanceStateIsDone(): Boolean {
        if (state == CoroutineState.Ready) {
            val next = nextStep
            if (next == null) {
                state = CoroutineState.Done
                return true
            } else {
                subActions.forEach {
                    it.advanceState()
                }
                nextStep = null // Keep track of state using null
                next.resume(Unit)
            }
        }
        return false
    }

    override suspend fun <T : Action> T.unaryPlus() {
        if (locked) {
            subActions.add(this)
        } else {
            subActions.add(this)
        }
    }

    override suspend fun cancel() {
        for (subAction in subActions) {
            subAction.interrupt()
        }
    }

    override suspend fun delay(seconds: Number) {
        TODO()
    }

    override suspend fun lock() {
        locked = true
    }

    override suspend fun free() {
        locked = false
    }

    override suspend fun skip() {
        suspendCoroutine<Unit> { continuation ->
            nextStep = continuation
        }
    }

    override suspend fun parallel(block: suspend ActionCoroutine.() -> Unit) {
        TODO("not implemented")
    }
}

@ActionDSL
operator fun String.not() {
    println(this)
}

@ActionDSL
operator fun Number.not() {
    println(this)
}

@ActionDSL
fun routineOf(debug: Boolean = false, block: suspend ActionCoroutine.() -> Unit) = Routine(debug, block)

@ActionDSL
suspend inline fun ActionCoroutine.sequential(block: () -> Unit) {
    lock()
    block()
    free()
}

class Routine(
        val debug: Boolean,
        val block: suspend ActionCoroutine.() -> Unit
)

val synchronizedControl = SynchronizedControl()

@ActionDSL
infix fun Notifier.run(action: Action) {
    cancel()
    synchronizedControl.setAction(action)
}

@Suppress("unused")
@ActionDSL
fun Notifier.cancel() {
    synchronizedControl.interrupt()
}

@Suppress("unused")
@ActionDSL
inline fun <T> RobotBase.using(t: T, block: T.() -> Unit) {
    synchronizedControl.updateActions()
    block(t)
}