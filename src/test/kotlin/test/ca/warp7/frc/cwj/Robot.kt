package test.ca.warp7.frc.cwj

import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.experimental.command.CommandScheduler
import test.ca.warp7.frc.sample.BaseIO
import test.ca.warp7.frc.sample.ioInstance

class Robot : TimedRobot(0.2) {
    private val io: BaseIO = ioInstance()
    private val scheduler: CommandScheduler = CommandScheduler.getInstance()
    private val lock = Any()

    private val notifier = Notifier {
        synchronized(this) {
            io.readInputs()
            scheduler.run()
            io.writeOutputs()
        }
    }

    override fun robotInit() {
        println("Hello me is robit!")
        notifier.startPeriodic(0.01)
    }

    override fun disabledInit() {
        synchronized(this) {
            scheduler.disable()
            scheduler.cancelAll()
            io.disable()
        }
    }

    override fun teleopInit() {
        synchronized(this) {
            scheduler.enable()
            io.enable()
        }
    }

}