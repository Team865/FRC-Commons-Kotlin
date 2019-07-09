package ca.warp7.frc.action.coroutine

import ca.warp7.frc.action.dispatch.ActionDSL


private fun notManaged(thisAction: Action): Nothing {
    val st = Thread.currentThread().stackTrace;
    val se = st[3].methodName
    error("$thisAction is not a managed action; unable to call $se")
}

@ActionDSL
operator fun String.not() {
    println(this)
}

@ActionDSL
operator fun Number.not() {
    println(this)
}

@ActionDSL
suspend inline infix fun <T : Action> Dispatch<T>.with(block: T.() -> Unit): Dispatch<T> {
    block(action)
    delay(2)
    return this
}

@ActionDSL
suspend inline infix fun <T : Action> Dispatch<T>.await(block: T.() -> Boolean) {
    block(action)
    cancel()
    delay(2)
}

suspend fun delay(n: Int) {
    TODO()
}

//@ActionDSL
//fun <T : Action> T.dispatch(
//        routine: Routine? = null,
//        debug: Boolean = false,
//        block: suspend DispatchScope.() -> Unit = {}
//): Dispatch<T> = if (routine == null) {
//    manager.dispatch(this, debug, block)
//} else {
//    manager.dispatch(this, routine.debug, routine.block)
//}

@ActionDSL
fun routineOf(debug: Boolean = false, block: suspend DispatchScope.() -> Unit) = Routine(debug, block)

@ActionDSL
suspend inline fun DispatchScope.sequential(block: () -> Unit) {
    lock()
    block()
    free()
}
