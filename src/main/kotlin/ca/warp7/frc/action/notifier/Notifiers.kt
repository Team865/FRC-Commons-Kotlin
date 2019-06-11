package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.*
import edu.wpi.first.wpilibj.Notifier
import kotlinx.coroutines.delay

private var ioLoop: IONotifier? = null

private val Notifier.delegate: ActionNotifier
    get() = this as? ActionNotifier ?: error("Notifier is not a delegate")


val IOAction.notifier: Notifier
    get() {
        val loop = ioLoop
        if (loop != null) return loop
        val newLoop = IONotifier(this)
        ioLoop = newLoop
        return newLoop
    }

@ActionDSL
infix fun Notifier.run(action: Action) {
    delegate.setAction(action)
}

@ActionDSL
fun Notifier.cancel() {
    delegate.cancelAll()
}

@ActionDSL
fun <T: Action> T.dispatch(
        join: Dispatch<T>? = null,
        routine: Routine? = null,
        debug: Boolean = false,
        block: suspend DispatchScope.() -> Unit = {}
): Dispatch<T> {
    if (routine != null) {
        DispatchCoroutine(routine.block)
    } else {
        DispatchCoroutine(Routine(debug, block).block)
    }
    if (join != null) {
        return join
    }
    return Dispatch(this)
}

@ActionDSL
fun routine(debug: Boolean = false, block: suspend DispatchScope.() -> Unit) = Routine(debug, block)

@ActionDSL
suspend inline infix fun <T: Action> Dispatch<T>.finally(block: T.() -> Unit): Dispatch<T> {
    block(action)
    delay(2)
    return this
}

@ActionDSL
suspend inline infix fun <T: Action> Dispatch<T>.with(block: T.() -> Unit): Dispatch<T> {
    block(action)
    delay(2)
    return this
}

@ActionDSL
suspend inline infix fun <T: Action> Dispatch<T>.await(block: T.() -> Boolean){
    block(action)
    tryCancel()
    delay(2)
}

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