package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.run
import kotlinx.coroutines.delay

class Autonomous : Action {
    override val shouldFinish: Boolean
        get() = false
    private val io = ioInstance()


    override fun update() {

        run {
            delay(3)
        }
    }
}