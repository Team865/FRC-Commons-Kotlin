package test.ca.warp7.frc.sample

import ca.warp7.frc.action.coroutine.Action


class Teleop : Action() {

    private val io = ioInstance()

    override fun shouldFinish(): Boolean {
        return false
    }

    override fun update() {
        io.run {
        }
    }
}