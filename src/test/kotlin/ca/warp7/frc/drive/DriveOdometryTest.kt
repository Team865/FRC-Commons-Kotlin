package ca.warp7.frc.drive

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Rotation2D.Companion.fromDegrees
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DriveOdometryTest {

    private val odometry = DriveOdometry(Rotation2D(), Pose2D())

    @Test
    fun testOdometryWithEncoderDistances() {
        odometry.resetPosition(Pose2D(), fromDegrees(45.0))
        val pose = odometry.update(fromDegrees(135.0), 0.0, 5 * Math.PI)
        assertEquals(pose.translation.x, 5.0, kEpsilon)
        assertEquals(pose.translation.y, 5.0, kEpsilon)
        assertEquals(pose.rotation.degrees(), 90.0, kEpsilon)
    }

    companion object {
        private const val kEpsilon = 1E-9
    }
}