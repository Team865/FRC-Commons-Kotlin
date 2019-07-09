@file:Suppress("unused")

package ca.warp7.frc.action.coroutine

import ca.warp7.frc.action.dispatch.ActionDSL
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase


@ActionDSL
infix fun Notifier.run(action: Action) {
    cancel()
    Action.setAction(action)
}

@ActionDSL
fun Notifier.cancel() {
    Action.interrupt()
}

@ActionDSL
inline fun <T> RobotBase.using(t: T, block: T.() -> Unit) {
    Action.updateActions()
    block(t)
}