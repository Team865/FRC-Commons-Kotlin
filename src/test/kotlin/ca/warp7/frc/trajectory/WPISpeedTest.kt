package ca.warp7.frc.trajectory

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator.generateTrajectory


object WPISpeedTest {
    fun nothingTest() {
        val a = System.nanoTime().toDouble()
        val config = TrajectoryConfig(3.0, 2.5)
        config.setKinematics(DifferentialDriveKinematics(1.0))
        for (i in 5..499) {
            generateTrajectory(
                    listOf(Pose2d(), Pose2d((i * 2).toDouble(), i.toDouble(), Rotation2d())),
                    config
            )
        }
        println((System.nanoTime() - a) / 1E9)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        nothingTest()
    }
}