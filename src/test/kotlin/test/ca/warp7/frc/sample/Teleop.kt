package test.ca.warp7.frc.sample

import ca.warp7.frc.Action
import ca.warp7.frc.ExperimentalActionDSL

@UseExperimental(ExperimentalActionDSL::class)
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