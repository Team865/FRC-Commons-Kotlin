package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action

class Autonomous: Action {
    override val shouldFinish: Boolean
        get() = false
}