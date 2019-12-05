package ca.warp7.frc.swerve

import ca.warp7.frc.geometry.Rotation2D

class SwerveWheels(
        val leftFront: Rotation2D,
        val rightFront: Rotation2D,
        val leftRear: Rotation2D,
        val rightRear: Rotation2D
) {
    operator fun times(by: Double): SwerveWheels {
        return SwerveWheels(
                leftFront * by,
                rightFront * by,
                leftRear * by,
                rightRear * by
        )
    }

    operator fun div(by: Double): SwerveWheels {
        return times(1.0 / by)
    }

    override fun toString(): String {
        return "SwerveWheels(LF=${leftFront.translation()}, RF=${rightFront.translation()}, LR=${leftRear.translation()}, RR=${rightRear.translation()})"
    }
}