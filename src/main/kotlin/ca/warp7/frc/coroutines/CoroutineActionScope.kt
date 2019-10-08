package ca.warp7.frc.coroutines

/**
 * Defines the scope in which a coroutine action can run
 */
@ExperimentalCoroutineAction
interface CoroutineActionScope<T> {

    /**
     * Suspends the current coroutine action for a single cycle
     */
    suspend fun nextCycle(): Boolean

    /**
     * Create another [CoroutineActionScope] and defer its execution
     * until it is called
     */
    fun <S> deferred(
            initialState: S,
            name: String = "",
            block: suspend CoroutineActionScope<S>.() -> Unit
    ): DeferredAction<S>

    /**
     * Return the elapsed time of this coroutine
     */
    fun elapsed(): Double

    /**
     * Set the state of this coroutine
     */
    fun setState(state: T)
}