@file:Suppress("unused")

package ca.warp7.frc.action.coroutine

import ca.warp7.frc.action.dispatch.ActionDSL
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase

private var currentAction: Action = object: Action() {

}
private var finished = false

fun updateActions() {
    if (!finished) {
        if (currentAction.shouldFinish()) {
            finished = true
            currentAction.lastCycle()
        } else {
            currentAction.update()
        }
    }
}

@ActionDSL
infix fun Notifier.run(action: Action) {
    cancel()
    val m = ManagedAction(action)
    m.firstCycle()
    currentAction = m
}

@ActionDSL
fun Notifier.cancel() {
    if (!finished) {
        currentAction.stop()
    }
}

@ActionDSL
inline fun <T> RobotBase.using(t: T, block: T.() -> Unit) {
    updateActions()
    block(t)
}