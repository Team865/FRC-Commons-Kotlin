package ca.warp7.frc.action.coroutine

interface Dispatch<T : Action>{
    val action: T
    fun cancel()
}