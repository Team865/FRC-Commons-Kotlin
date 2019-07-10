package ca.warp7.frc.action.coroutine

import ca.warp7.frc.action.coroutine.CycleState.*
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import kotlin.coroutines.*

/**
 * An [Action] defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the [firstCycle],
 * [update], [lastCycle], [interrupt], and [shouldFinish] methods.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class Action {

    /**
     * Run code once when the action is started, usually for set up.
     * This method is called first before shouldFinish
     */
    protected open fun firstCycle() {
        warnThis("is started (first cycle); no override implementation")
    }

    /**
     * Periodically updates the action
     */
    protected open fun update() {}

    /**
     * Run the last cycle
     */
    protected open fun lastCycle() {
        warnThis("is stopped (last cycle); no override implementation")
    }

    /**
     * Interrupt the action
     */
    protected open fun interrupt() {
        warnThis("is interrupted; no override implementation")
    }

    /**
     * Returns whether or not the code has finished execution.
     */
    open fun shouldFinish(): Boolean {
        return false
    }

    /**
     * Sends a warning message
     */
    open fun warnThis(msg: String) {
        println("ERROR $this $msg")
    }

    private val coroutines: MutableList<DispatchCoroutine> = mutableListOf()

    private var cycleState = FirstCycle

    private var coroutineCycleCompleted = false

    @ActionDSL
    protected fun Routine.run() {
        dispatch(debug, block)
    }

    @ActionDSL
    protected fun dispatch(debug: Boolean = false,
                           block: suspend ActionCoroutine.() -> Unit) {
        if (cycleState == Periodic) {
            val coroutine = DispatchCoroutine(debug)
            coroutine.nextStep = block.createCoroutine(coroutine, coroutine)
            coroutines.add(coroutine)
        }
    }

    @ActionDSL
    protected fun runFinally(block: () -> Unit) {
        if (cycleState == Periodic) {
            runCoroutineCycle()
            block()
        }
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

    private fun advanceState() {
        when (cycleState) {
            FirstCycle -> {
                firstCycle()
                cycleState = Periodic
            }
            Periodic -> {
                if (shouldFinish()) {
                    lastCycle()
                    cycleState = Done
                } else {
                    coroutineCycleCompleted = false
                    update()
                    runCoroutineCycle()
                }
            }
            Done -> {
                warnThis("is already done; cannot advance state")
                cycleState = Idle
            }
            Idle -> {

            }
        }
    }

    private fun stop() {
        when (cycleState) {
            FirstCycle -> {

            }
            Periodic -> {

            }
            Done -> {

            }
            Idle -> {

            }
        }
    }

    protected var name: String = javaClass.simpleName

    override fun toString(): String {
        if (coroutines.isEmpty()) {
            return name
        }
        return name + coroutines.joinToString(", ", "[", "]")
    }


    class Control {
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
}

private enum class CoroutineState {
    Ready, Done
}

private enum class CycleState {
    FirstCycle, Periodic, Done, Idle
}

private class DispatchCoroutine(val debug: Boolean = false) : ActionCoroutine, Continuation<Unit> {

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
                nextStep = null // Keep track of state using null
                next.resume(Unit)
            }
        }
        return false
    }

    override suspend fun <T : Action> T.unaryPlus(): Dispatch<T> {
        if (locked) {
            subActions.add(this)
        }
        TODO()
    }

    override suspend fun await(vararg dispatch: Dispatch<*>) {
    }

    override suspend fun cancel() {
    }

    override suspend fun delay(seconds: Number): Dispatch<Action> {
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
suspend inline infix fun <T : Action> Dispatch<T>.with(block: T.() -> Unit): Dispatch<T> {
    block(action)
    delay(2)
    return this
}

@ActionDSL
suspend inline infix fun <T : Action> Dispatch<T>.await(block: T.() -> Boolean) {
    block(action)
    cancel()
    delay(2)
}

suspend fun delay(n: Int) {
    TODO()
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

val actionControl = Action.Control()

@ActionDSL
infix fun Notifier.run(action: Action) {
    cancel()
    actionControl.setAction(action)
}

@Suppress("unused")
@ActionDSL
fun Notifier.cancel() {
    actionControl.interrupt()
}

@Suppress("unused")
@ActionDSL
inline fun <T> RobotBase.using(t: T, block: T.() -> Unit) {
    actionControl.updateActions()
    block(t)
}