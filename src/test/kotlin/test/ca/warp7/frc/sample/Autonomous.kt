package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.notifier.*
import ca.warp7.frc.feet
import ca.warp7.frc.inches

class Autonomous : Action {
    override val shouldFinish: Boolean
        get() = false
    private val io = ioInstance()

    private val routine1 = routine {

        sequential {
            +DriveTrajectory(88.inches + 1.feet)
            +QuickTurn(-90.0) with {
                !currentAngle
            }
            +DriveTrajectory(65.inches)
            +QuickTurn(70.0)
        }


        +LiftSetpoint(8.0) with {

        }

        sequential {
            +DriveTrajectory(50.inches)
            io.grabbing = false
            io.pushing = true
            delay(0.5)
            io.pushing = false
        }

        cancel()

        parallel {
            +DriveTrajectory((-3).feet)
            +QuickTurn(-160.0) with {
                !currentAngle
            }
            +DriveTrajectory(160.inches)
        }

        parallel {
            delay(0.5)
            +LiftSetpoint(5.0)
        }

        await()
    }


    class LiftSetpoint(height: Double) : Action {
        override val shouldFinish: Boolean
            get() = false

        fun stopAll() {

        }
    }

    class DriveTrajectory(dist: Number) : Action {
        override val shouldFinish: Boolean
            get() = false
    }

    class QuickTurn(angle: Number) : Action {
        var currentAngle: Double = 0.0
        override val shouldFinish: Boolean
            get() = false
    }


    override fun update() {
        dispatch(routine1)
        finally {

        }
    }
}