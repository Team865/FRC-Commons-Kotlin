package ca.warp7.frc.action.dispatch

import ca.warp7.frc.action.Action

interface ActionManager {
    fun <T: Action> dispatch(
            action: T,
            debug: Boolean,
            block: suspend DispatchScope.() -> Unit
    ): Dispatch<T>

    fun elapsed(): Double

    fun setEpoch()

    fun dt(): Double
}