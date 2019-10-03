package ca.warp7.frc.coroutines


@ExperimentalCoroutineAction
@CoroutineDSL
fun routineOf(debug: Boolean = false, block: suspend CoroutineActionScope.() -> Unit) = Routine(debug, block)

@ExperimentalCoroutineAction
@CoroutineDSL
suspend inline fun CoroutineActionScope.sequential(block: () -> Unit) {
    lock()
    block()
    free()
}

@ExperimentalCoroutineAction
@CoroutineDSL
fun CoroutineAction.runRoutine(debug: Boolean = false, block: suspend CoroutineActionScope.() -> Unit) {
    Routine(debug, block).run()
}