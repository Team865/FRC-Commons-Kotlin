package ca.warp7.frc.action.dispatch

import ca.warp7.frc.action.Action

interface Dispatch<T : Action>{
    val action: T
    fun cancel()
}