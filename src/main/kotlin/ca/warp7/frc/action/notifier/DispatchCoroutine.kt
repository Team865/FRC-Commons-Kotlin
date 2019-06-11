package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import kotlin.coroutines.*

class DispatchCoroutine(block: suspend DispatchScope.() -> Unit): DispatchScope, Continuation<Unit> {
    override suspend fun cancel() {
        TODO("not implemented")
    }

    override suspend fun lock() {
        TODO("not implemented")
    }

    override suspend fun free() {
        TODO("not implemented")
    }

    private var nextStep: Continuation<Unit> = block.createCoroutine(this, this)


    override suspend fun delay(seconds: Number): Dispatch<Action> {
        TODO("not implemented")
    }

    override suspend fun <T : Action> T.unaryPlus(): Dispatch<T> {
        start(this)
        return Dispatch(this)
    }

    override suspend fun await(vararg dispatch: Dispatch<*>) {
        TODO("not implemented")
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
    }

    private suspend fun start(action: Action) {
        return suspendCoroutine {
            nextStep = it
        }
    }
}