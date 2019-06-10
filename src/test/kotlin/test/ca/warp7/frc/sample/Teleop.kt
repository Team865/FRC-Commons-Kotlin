package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action

class Teleop : Action {

    private val io = ioInstance()

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        io.run {
        }
    }
}