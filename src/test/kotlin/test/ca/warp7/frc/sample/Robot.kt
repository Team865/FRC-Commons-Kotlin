package test.ca.warp7.frc.sample

import ca.warp7.frc.action.notifier.cancel
import ca.warp7.frc.action.notifier.run
import ca.warp7.frc.action.notifier.using
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot

class Robot : TimedRobot(0.2) {

    private val io: BaseIO = ioInstance()
    private val notifier = Notifier {
        using(io.readInputs()) { io.writeOutputs() }
    }

    override fun robotInit() {
        println("Hello me is robit!")
        io.initialize()
        notifier.startPeriodic(0.01)
    }

    override fun disabledInit() {
        io.disable()
        notifier.cancel()
    }

    override fun autonomousInit() {
        io.enable()
        notifier run Autonomous()
    }

    override fun teleopInit() {
        io.enable()
        notifier run Teleop()
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