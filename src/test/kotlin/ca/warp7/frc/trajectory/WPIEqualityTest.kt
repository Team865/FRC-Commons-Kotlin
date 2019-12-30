package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import edu.wpi.first.wpilibj.trajectory.Trajectory
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator
import edu.wpi.first.wpilibj.trajectory.constraint.CentripetalAccelerationConstraint
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveKinematicsConstraint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WPIEqualityTest {
    @Test
    fun testTrajectoryGeneratorEqual() {
        val t1 = TrajectoryGenerator.generateTrajectory(listOf(Pose2d(), Pose2d(5.0, 2.0, Rotation2d())),
                TrajectoryConfig(3.0, 3.0)
                        .addConstraint(DifferentialDriveKinematicsConstraint(
                                DifferentialDriveKinematics(2.0),
                                3.0)
                        )
                        .addConstraint(CentripetalAccelerationConstraint(4.0))
        )

        val p = parameterizedSplinesOf(listOf(Pose2D(), Pose2D(5.0, 0.0, 0.0)))
        parameterizeTrajectory(p, 1.0, 3.0,
                3.0, 4.0, Double.POSITIVE_INFINITY)

        assertTrajectoryEqual(p, t1.states)
    }

    private fun assertTrajectoryEqual(t1: List<TrajectoryState>, t2: List<Trajectory.State>) {
        assertEquals(t1.size, t2.size)
        for (i in t1.indices) {
            val s1 = t1[i]
            val s2 = t2[i]
            assertEquals(s1.v, s2.velocityMetersPerSecond, 1E6)
            assertEquals(s1.curvature, s2.curvatureRadPerMeter, 1E6)
            assertEquals(s1.pose.translation.x, s2.poseMeters.translation.x, 1E6)
            assertEquals(s1.pose.translation.y, s2.poseMeters.translation.y, 1E6)
            assertEquals(s1.pose.rotation.cos, s2.poseMeters.rotation.cos, 1E6)
            assertEquals(s1.pose.rotation.sin, s2.poseMeters.rotation.sin, 1E6)
        }
    }
}