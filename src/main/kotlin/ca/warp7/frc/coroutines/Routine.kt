package ca.warp7.frc.coroutines

@ExperimentalActionDSL
class Routine(
        val debug: Boolean,
        val block: suspend ActionCoroutine.() -> Unit
)