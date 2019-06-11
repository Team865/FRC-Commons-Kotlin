package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import kotlin.coroutines.*

class DispatchCoroutine(block: suspend DispatchScope.() -> Unit): DispatchScope, Continuation<Unit> {

    private var nextStep: Continuation<Unit> = block.createCoroutine(this, this)

    override suspend fun Action.unaryPlus() {
        TODO("not implemented")
    }

    override suspend fun await() {
        TODO("not implemented")
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
    }

    override suspend fun start(action: Action) {
        return suspendCoroutine {
            nextStep = it
        }
    }
}