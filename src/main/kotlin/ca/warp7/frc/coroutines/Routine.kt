package ca.warp7.frc.coroutines

@ExperimentalCoroutineAction
class Routine(
        val debug: Boolean,
        val block: suspend CoroutineActionScope.() -> Unit
)