package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.notifier.dispatch

class Autonomous : Action {
    override val shouldFinish: Boolean
        get() = false
    private val io = ioInstance()


    override fun update() {
        dispatch {
            start(Teleop())
            +Teleop()
            await()
        }
    }
}