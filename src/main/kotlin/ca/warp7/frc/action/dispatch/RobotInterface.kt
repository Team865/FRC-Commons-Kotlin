@file:Suppress("unused")

package ca.warp7.frc.action.dispatch

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.NoAction
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase

private var currentAction: Action = NoAction
private var finished = false

fun updateActions() {
    if (!finished) {
        if (currentAction.shouldFinish) {
            finished = true
            currentAction.stop(false)
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
        currentAction.stop(true)
    }
}

@ActionDSL
inline fun <T> RobotBase.using(t: T, block: T.() -> Unit) {
    updateActions()
    block(t)
}