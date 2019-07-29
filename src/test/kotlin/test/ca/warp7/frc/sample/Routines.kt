package test.ca.warp7.frc.sample

import ca.warp7.frc.*

@UseExperimental(ExperimentalActionDSL::class)
object Routines {
    private val io = ioInstance()
    val routine1 = routineOf {

        sequential {
            +DriveTrajectory(88.inches + 1.feet)
            +QuickTurn(-90.0)
            +DriveTrajectory(65.inches)
            +QuickTurn(70.0)
        }

        +LiftSetpoint(8.0)

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
            +QuickTurn(-160.0)
            +DriveTrajectory(160.inches)
        }

        parallel {
            delay(0.5)
            +LiftSetpoint(5.0)
        }
    }

    class LiftSetpoint(height: Double) : Action() {
        fun stopAll() {

        }
    }

    class DriveTrajectory(dist: Number) : Action() {
    }

    class QuickTurn(angle: Number) : Action() {
        var currentAngle: Double = 0.0
    }

}