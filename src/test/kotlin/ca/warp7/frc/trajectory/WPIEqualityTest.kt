package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator
import edu.wpi.first.wpilibj.trajectory.constraint.CentripetalAccelerationConstraint
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveKinematicsConstraint
import org.junit.jupiter.api.Test

class WPIEqualityTest {
    @Test
    fun testTrajectoryGeneratorEqual() {
        val t1 = TrajectoryGenerator.generateTrajectory(listOf(Pose2d(), Pose2d(5.0, 0.0, Rotation2d())),
                TrajectoryConfig(3.0, 3.0)
                        .addConstraint(DifferentialDriveKinematicsConstraint(
                                DifferentialDriveKinematics(2.0),
                                3.0)
                        )
                        .addConstraint(CentripetalAccelerationConstraint(4.0))
        )

        println(t1)

        val p = parameterizedSplinesOf(listOf(Pose2D(), Pose2D(5.0, 0.0, 0.0)))
        parameterizeTrajectory(p, 1.0, 3.0,
                3.0, 4.0, Double.POSITIVE_INFINITY)
        println(p.joinToString("\n"))
    }
}