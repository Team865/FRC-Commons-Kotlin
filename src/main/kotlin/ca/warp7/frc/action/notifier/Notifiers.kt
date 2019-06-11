package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionDSL
import ca.warp7.frc.action.IOAction
import ca.warp7.frc.action.queue
import edu.wpi.first.wpilibj.Notifier
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

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

fun Action.dispatch(block: suspend DispatchScope.() -> Unit): Action {
    DispatchCoroutine(block)
    return this
}

@ActionDSL
fun runSequence(f: suspend () -> Unit): Action {
    return queue { }
}