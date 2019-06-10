package test.ca.warp7.frc.sample

import ca.warp7.frc.action.*
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot

class Robot : TimedRobot(0.2) {

    private val io: BaseIO = ioInstance()

    override fun robotInit() {
        println("Hello me is robit!")
        io.initialize()
        io.notifier.startPeriodic(0.01)
    }

    override fun disabledInit() {
        io.disable()
        io.cancel()
    }

    override fun autonomousInit() {
        io.enable()
        Autonomous().run()
    }

    override fun teleopInit() {
        io.enable()
        Teleop().run()
    }

    override fun disabledPeriodic() {}
    override fun autonomousPeriodic() {}
    override fun teleopPeriodic() {}
    override fun robotPeriodic() {}

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            RobotBase.startRobot { Robot() }
        }
    }
}