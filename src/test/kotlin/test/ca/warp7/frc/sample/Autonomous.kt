package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.dispatch.*
import ca.warp7.frc.feet
import ca.warp7.frc.inches
import test.ca.warp7.frc.sample.Routines.routine1

class Autonomous : Action {
    override val shouldFinish: Boolean
        get() = false

    override fun firstCycle() {
        setEpoch()
    }

    override fun update() {
        dispatch(routine1)
        finally {

        }
    }
}