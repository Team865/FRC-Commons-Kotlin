package ca.warp7.frc.coroutines

import kotlin.coroutines.*

@ExperimentalActionDSL
internal class CoroutineWithContinuation(
        val handle: Int,
        val action: Action,
        val debug: Boolean = false
) : ActionCoroutine, Continuation<Unit> {

    var nextStep: Continuation<Unit>? = null

    var locked = false

    var state = CoroutineState.Ready

    val subActions: MutableList<Action> = mutableListOf()

    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun toString(): String {
        return ""
    }

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
        action.runRoutine(debug, block)
    }
}