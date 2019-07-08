package ca.warp7.frc.action.coroutine

class Routine(
        val debug: Boolean,
        val block: suspend DispatchScope.() -> Unit
)