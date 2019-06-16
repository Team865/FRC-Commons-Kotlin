package ca.warp7.frc.action.dispatch

class Routine(
        val debug: Boolean,
        val block: suspend DispatchScope.() -> Unit
)