package ca.warp7.frc.action.coroutine

import ca.warp7.frc.action.coroutine.Action.CycleState.*
import ca.warp7.frc.action.dispatch.ActionDSL
import kotlin.coroutines.*

@Suppress("MemberVisibilityCanBePrivate")
open class Action {

    open fun firstCycle() {}

    open fun update() {}

    open fun lastCycle() {}

    open fun interrupt() {}

    open fun shouldFinish(): Boolean {
        return false
    }

    @ActionDSL
    protected fun Routine.run() {
        dispatch(debug, block)
    }

    @ActionDSL
    protected fun dispatch(debug: Boolean = false,
                 block: suspend DispatchScope.() -> Unit) {
        if (cycleState == Periodic) {
            val coroutine = Coroutine(debug)
            coroutine.nextStep = block.createCoroutine(coroutine, coroutine)
            coroutines.add(coroutine)
        }
    }

    @ActionDSL
    protected fun finally(block: () -> Unit) {
        if (cycleState == Periodic) {
            runCoroutineCycle()
            block()
        }
    }

    private var coroutineCycleCompleted = false

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

    private enum class CycleState {
        FirstCycle, Periodic, Done, DoneAfterWarning
    }

    private val coroutines: MutableList<Coroutine> = mutableListOf()

    private var cycleState = FirstCycle

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
                println("ERROR: $this is already done; cannot advance state")
                cycleState = DoneAfterWarning
            }
            DoneAfterWarning -> {

            }
        }
    }

    private fun stop() {
        when (cycleState) {
            FirstCycle -> TODO()
            Periodic -> TODO()
            Done -> TODO()
            DoneAfterWarning -> TODO()
        }
    }

    protected var name: String = javaClass.simpleName

    override fun toString(): String {
        if (coroutines.isEmpty()) {
            return name
        }
        return name + coroutines.joinToString(", ", "[", "]")
    }

    private enum class CoroutineState {
        Ready, Done
    }

    private class Coroutine(val debug: Boolean = false) : DispatchScope, Continuation<Unit> {

        override fun resumeWith(result: Result<Unit>) {
            result.getOrThrow()
        }

        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        var nextStep: Continuation<Unit>? = null

        var locked = false

        var state = CoroutineState.Ready

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

        override suspend fun parallel(block: suspend DispatchScope.() -> Unit) {
            TODO("not implemented")
        }
    }

    companion object {
        private var currentAction = Action()

        internal fun setAction(action: Action) {
            currentAction = action
            currentAction.advanceState()
        }

        internal fun interrupt() {
            currentAction.stop()
        }


        fun updateActions() {
            currentAction.advanceState()
        }
    }
}