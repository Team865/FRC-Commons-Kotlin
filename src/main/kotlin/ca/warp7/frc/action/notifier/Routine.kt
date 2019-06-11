package ca.warp7.frc.action.notifier

class Routine(
        val debug: Boolean,
        val block: suspend DispatchScope.() -> Unit
)