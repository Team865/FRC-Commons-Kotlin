package ca.warp7.frc.coroutines

@ExperimentalCoroutineAction
@CoroutineDSL
interface CoroutineActionScope {

    @CoroutineDSL
    suspend operator fun <T : CoroutineAction> T.unaryPlus()

    @CoroutineDSL
    suspend fun cancel()

    @CoroutineDSL
    suspend fun delay(seconds: Number)

    @CoroutineDSL
    suspend fun lock()

    @CoroutineDSL
    suspend fun free()

    @CoroutineDSL
    suspend fun skip()

    @CoroutineDSL
    suspend fun parallel(block: suspend CoroutineActionScope.() -> Unit)
}