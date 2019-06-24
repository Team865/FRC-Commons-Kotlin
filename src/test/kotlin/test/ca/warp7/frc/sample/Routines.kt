package test.ca.warp7.frc.sample

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.dispatch.*
import ca.warp7.frc.feet
import ca.warp7.frc.inches

object Routines {
    private val io = ioInstance()
    val routine1 = routine {

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
            delay(0.5) with {
                !elapsed
            }
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
            delay(0.5) with {
            }
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

    suspend fun CommandScope.quickTurn(angle: Double) {
        whileActive {
            val actual = 0
            val error = angle - actual
            val left = -error*0.1
            val right = error *0.1
        }
        val left = 0
        val right = 0
    }

    @ActionDSL
    suspend fun CommandScope.whileActive(block: () -> Unit): Boolean {
        return true
    }
}

typealias CommandScope = DispatchScope