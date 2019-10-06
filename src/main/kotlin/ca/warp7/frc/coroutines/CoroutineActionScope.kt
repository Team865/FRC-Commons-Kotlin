package ca.warp7.frc.coroutines

@ExperimentalCoroutineAction
interface CoroutineActionScope<T> {

    suspend fun nextCycle(): Boolean

    fun <S> deferred(
            initialState: S,
            name: String = "",
            block: suspend CoroutineActionScope<S>.() -> Unit
    ): DeferredAction<S>

    fun elapsed(): Double

    fun setTiming(func: () -> Double)

    fun setState(state: T)
}