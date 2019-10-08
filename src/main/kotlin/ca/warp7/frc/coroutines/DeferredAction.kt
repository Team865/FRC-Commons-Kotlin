package ca.warp7.frc.coroutines

@ExperimentalCoroutineAction
interface DeferredAction<T> {

    val state: T

    fun launch(): DeferredAction<T>

    fun isDone(): Boolean

    suspend fun await()

    suspend fun awaitState(func: (T) -> Boolean)

    fun finally(func: (T) -> Unit)
}