package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionDSL

interface DispatchScope {
    @ActionDSL
    suspend fun start(action: Action)

    suspend operator fun Action.unaryPlus()

    @ActionDSL
    suspend fun await()
}