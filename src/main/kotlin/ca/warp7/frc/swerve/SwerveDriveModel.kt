package ca.warp7.frc.swerve

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.radians

@Suppress("MemberVisibilityCanBePrivate")
class SwerveDriveModel(
        val length: Double, // m
        val width: Double // m
) {


    companion object {
        const val kEpsilon = 1E-9
    }

    /**
     * Forward kinematics
     */
    fun solve(wheels: SwerveWheels): Pose2D {
        val a = (wheels.rightRear.sin + wheels.leftRear.sin) / 2
        val b = (wheels.leftFront.sin + wheels.rightFront.sin) / 2
        val c = (wheels.rightFront.cos + wheels.rightRear.cos) / 2
        val d = (wheels.leftFront.cos + wheels.leftRear.cos) / 2
        val omega = ((b - a) / length + (d - c) / width) / 2
        val forward = (a + b) / 2
        val strafe = -(c + d) / 2
        return Pose2D(forward, strafe, omega.radians)
    }

    /**
     * Inverse kinematics
     */
    fun solve(transform: Pose2D): SwerveWheels {
        val omega = transform.rotation.radians()
        val forward = transform.translation.x
        val strafe = -transform.translation.y
        val a = forward - omega * width / 2
        val b = forward + omega * width / 2
        val c = strafe - omega * length / 2
        val d = strafe + omega * length / 2

        return SwerveWheels(
                leftFront = Rotation2D(b, d),
                rightFront = Rotation2D(b, c),
                leftRear = Rotation2D(a, d),
                rightRear = Rotation2D(a, c)
        )
    }
}