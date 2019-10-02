package ca.warp7.frc.coroutines

@ExperimentalActionDSL
@ActionDSL
interface ActionCoroutine {

    @ActionDSL
    suspend operator fun <T : Action> T.unaryPlus()

    @ActionDSL
    suspend fun cancel()

    @ActionDSL
    suspend fun delay(seconds: Number)

    @ActionDSL
    suspend fun lock()

    @ActionDSL
    suspend fun free()

    @ActionDSL
    suspend fun skip()

    @ActionDSL
    suspend fun parallel(block: suspend ActionCoroutine.() -> Unit)
}