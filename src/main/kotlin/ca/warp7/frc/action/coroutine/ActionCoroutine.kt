package ca.warp7.frc.action.coroutine

@ActionDSL
interface ActionCoroutine {

    @ActionDSL
    suspend operator fun <T : Action> T.unaryPlus(): Dispatch<T>

    @ActionDSL
    suspend fun await(vararg dispatch: Dispatch<*>)

    @ActionDSL
    suspend fun cancel()

    @ActionDSL
    suspend fun delay(seconds: Number): Dispatch<Action>

    @ActionDSL
    suspend fun lock()

    @ActionDSL
    suspend fun free()

    @ActionDSL
    suspend fun skip()

    @ActionDSL
    suspend fun parallel(block: suspend ActionCoroutine.() -> Unit)
}