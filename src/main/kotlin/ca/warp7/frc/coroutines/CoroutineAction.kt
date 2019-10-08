package ca.warp7.frc.coroutines

import ca.warp7.frc.action.Action
import ca.warp7.frc.coroutines.CoroutineAction.State.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

/**
 * Implements [CoroutineActionScope]
 *
 * See [SequenceBuilderIterator] for similar implementation
 */
@ExperimentalCoroutineAction
internal class CoroutineAction<T>(
        initialState: T,
        private val name: String,
        private val timing: () -> Double,
        private val func: suspend CoroutineActionScope<T>.() -> Unit
) :
        CoroutineActionScope<T>,
        Continuation<Unit>,
        Action {

    private enum class State {
        NotStarted,
        NotReady,
        ReadyForNextCycle,
        Done,
        Failed
    }

    // No context
    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    // called when the entire action is done
    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
        coroutineState = Done
    }

    private var coroutineState: State = NotReady
        set(value) {
            println("Setting state to $value")
            field = value
        }

    private var actionState: T = initialState

    private var nextStep: Continuation<Boolean>? = null

    private var startTime = 0.0

    override suspend fun nextCycle(): Boolean {
        coroutineState = ReadyForNextCycle
        return suspendCoroutineUninterceptedOrReturn { cont ->
            nextStep = cont
            COROUTINE_SUSPENDED
        }
    }

    override fun <S> deferred(
            initialState: S,
            name: String,
            block: suspend CoroutineActionScope<S>.() -> Unit
    ): DeferredAction<S> {
        TODO("nothing here yet")
    }

    override fun elapsed(): Double {
        return timing.invoke() - startTime
    }

    override fun setState(state: T) {
        actionState = state
    }

    override fun name(): String {
        return name
    }

    override fun shouldFinish(): Boolean {
        return nextStep == null
    }

    override fun update() {
    }


    /**
     * Calling [startCoroutine] executes any parts of the action
     * before a suspending call
     */
    override fun firstCycle() {
        if (coroutineState == NotStarted) {
            coroutineState = NotReady
            startTime = timing.invoke()
            func.startCoroutine(
                    receiver = this as CoroutineActionScope<T>,
                    completion = this as Continuation<Unit>
            )
        } else {
            coroutineState = Failed
            throw IllegalStateException("Coroutine already started; Cannot be started again")
        }
    }

    /**
     * Nothing to do in the last cycle
     */
    override fun lastCycle() {
    }

    /**
     * [nextCycle] will return true now
     */
    override fun interrupt() {
        if (coroutineState != Done) {
            nextStep?.resumeWith(Result.success(true))
            coroutineState = Done
        }
    }
}