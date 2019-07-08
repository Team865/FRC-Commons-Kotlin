package test.ca.warp7.frc.sample

import ca.warp7.frc.action.coroutine.Action
import ca.warp7.frc.action.coroutine.dispatch
import ca.warp7.frc.action.coroutine.finally
import ca.warp7.frc.action.coroutine.setEpoch
import test.ca.warp7.frc.sample.Routines.routine1

class Autonomous : Action() {
    override fun shouldFinish(): Boolean {
        return false
    }

    override fun firstCycle() {
        setEpoch()
    }

    override fun update() {
        dispatch(routine1)
        finally {

        }
    }
}