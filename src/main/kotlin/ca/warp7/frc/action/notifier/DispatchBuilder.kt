package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

internal val managers: MutableMap<Action, ActionManager> = ConcurrentHashMap()

private val Action.manager
    get() = managers[this] ?: notManaged(this)

private fun notManaged(thisAction: Action): Nothing {
    val st =Thread.currentThread().stackTrace;
    val se = st[3].methodName
    error("$thisAction is not a managed action; unable to call $se")
}


@ActionDSL
inline infix fun <T : Action> T.finally(block: () -> Unit) {
    block()
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
    tryCancel()
    delay(2)
}

@ActionDSL
fun <T : Action> T.dispatch(
        routine: Routine? = null,
        debug: Boolean = false,
        block: suspend DispatchScope.() -> Unit = {}
): Dispatch<T> = if (routine == null) {
    manager.dispatch(this, debug, block)
} else {
    manager.dispatch(this, routine.debug, routine.block)
}

@ActionDSL
fun routine(debug: Boolean = false, block: suspend DispatchScope.() -> Unit) = Routine(debug, block)

@ActionDSL
suspend inline fun DispatchScope.sequential(block: () -> Unit) {
    lock()
    block()
    free()
}

@ActionDSL
suspend fun DispatchScope.parallel(block: suspend DispatchScope.() -> Unit) {
    lock()
    DispatchCoroutine(block)
    free()
}