package test.ca.warp7.frc.sample

import ca.warp7.frc.action.coroutine.Action
import ca.warp7.frc.action.coroutine.sequential
import test.ca.warp7.frc.sample.Routines.routine1

class Autonomous : Action() {
    override fun shouldFinish(): Boolean {
        return false
    }

    override fun firstCycle() {
    }

    override fun update() {

        dispatch {
        }

        routine1.run()
    }
}