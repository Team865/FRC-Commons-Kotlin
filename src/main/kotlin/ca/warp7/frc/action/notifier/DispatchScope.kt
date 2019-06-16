package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action

@ActionDSL
interface DispatchScope {

    suspend operator fun <T: Action> T.unaryPlus(): Dispatch<T>

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
}