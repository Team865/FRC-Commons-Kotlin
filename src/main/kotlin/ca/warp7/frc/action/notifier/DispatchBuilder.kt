package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionDSL
import kotlinx.coroutines.delay
import kotlin.concurrent.withLock

@ActionDSL
suspend inline infix fun <T : Action> Dispatch<T>.finally(block: T.() -> Unit): Dispatch<T> {
    block(action)
    delay(2)
    return this
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
): Dispatch<T> {
    managersMutex.withLock {
        val maybeManager = managers.firstOrNull { it.manages(this) }
        if (maybeManager != null) {

        } else {

        }
    }

    if (routine != null) {
        DispatchCoroutine(routine.block)
    } else {
        DispatchCoroutine(Routine(debug, block).block)
    }
    return Dispatch(this)
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