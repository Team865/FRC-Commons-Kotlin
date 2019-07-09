package test.ca.warp7.frc.sample

import ca.warp7.frc.action.coroutine.*
import ca.warp7.frc.action.dispatch.*
import ca.warp7.frc.feet
import ca.warp7.frc.inches

object Routines {
    private val io = ioInstance()
    val routine1 = routineOf {

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

    class LiftSetpoint(height: Double) : Action() {
        fun stopAll() {

        }
    }

    class DriveTrajectory(dist: Number) : Action() {
    }

    class QuickTurn(angle: Number) : Action() {
        var currentAngle: Double = 0.0
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