package ca.warp7.frc.coroutines


@ExperimentalActionDSL
@ActionDSL
fun routineOf(debug: Boolean = false, block: suspend ActionCoroutine.() -> Unit) = Routine(debug, block)

@ExperimentalActionDSL
@ActionDSL
suspend inline fun ActionCoroutine.sequential(block: () -> Unit) {
    lock()
    block()
    free()
}

@ExperimentalActionDSL
@ActionDSL
fun Action.runRoutine(debug: Boolean = false, block: suspend ActionCoroutine.() -> Unit) {
    Routine(debug, block).run()
}