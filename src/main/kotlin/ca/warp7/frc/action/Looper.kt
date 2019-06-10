package ca.warp7.frc.action

import edu.wpi.first.wpilibj.Notifier
import kotlinx.coroutines.CoroutineScope

private var ioLoop: ActionNotifier? = null

class ActionNotifier(
        val io: IOAction
): Notifier(null) {
    fun update() {

    }
}


val IOAction.notifier: Notifier
    get() {
        val loop = ioLoop
        if (loop != null) return loop
        val newLoop = ActionNotifier(this)
        ioLoop = newLoop
        return newLoop
    }



@ActionDSL
infix fun Notifier.run(action: Action) {

}


@ActionDSL
fun Notifier.cancel() {
}

fun IOAction.cancel() {
    notifier.cancel()
}

fun IOAction.startPeriodic(period: Double) {
    notifier.startPeriodic(period)
}

infix fun IOAction.run(action: Action) {
    notifier.run(action)
}

@ActionDSL
fun Action.run(block: suspend CoroutineScope.() -> Unit) {

}


@ActionDSL
fun runSequence(f: suspend () -> Unit): Action {
    return queue {  }
}