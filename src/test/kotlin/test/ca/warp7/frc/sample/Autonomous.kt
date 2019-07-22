package test.ca.warp7.frc.sample

import ca.warp7.frc.Action
import ca.warp7.frc.runRoutine
import test.ca.warp7.frc.sample.Routines.routine1

class Autonomous : Action() {
    override fun shouldFinish(): Boolean {
        return false
    }

    override fun firstCycle() {
    }

    override fun update() {

        runRoutine {
            delay(3)
        }

        routine1.run()
    }
}