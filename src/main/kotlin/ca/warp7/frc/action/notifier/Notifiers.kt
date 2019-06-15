package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionDSL
import ca.warp7.frc.action.IOAction
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import kotlinx.coroutines.delay
import java.io.File


@ActionDSL
infix fun Notifier.run(action: Action) {
}

@ActionDSL
fun Notifier.cancel() {
}

fun RobotBase.updateActions() {
    this.isDisabled
}

@ActionDSL
inline fun <T> RobotBase.using(k: T, block: T.() -> Unit) {
    updateActions()
    block(k)
}