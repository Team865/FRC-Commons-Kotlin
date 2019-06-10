package ca.warp7.frc.action

import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase

private class WPIExt(runnable: () -> Unit) : Notifier(runnable) {
    override fun startPeriodic(period: Double) {
        println("hi")
        super.startPeriodic(period)
    }
}

interface IterScope {
    fun iterateActions()
}

@ActionDSL
val IOAction.notifier: Notifier get() {
    val notifier = WPIExt {
    }
    return notifier
}

@ActionDSL
fun IOAction.cancel() {

}

@ActionDSL
fun Action.run() {

}