package ca.warp7.frc.drive

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Twist2D

class DriveOdometry(
        gyroAngle: Rotation2D,
        private var pose: Pose2D = Pose2D.identity
) {
    private var prevLeftDist = 0.0
    private var prevRightDist = 0.0
    private var prevAngle = Rotation2D.identity
    private var gyroOffset = pose.rotation - gyroAngle

    /**
     * Returns the position of the robot on the field.
     *
     * @return The pose of the robot (x and y are in meters).
     */
    fun pose(): Pose2D {
        return pose
    }

    /**
     * Resets the robot's position on the field.
     *
     *
     * You NEED to reset your encoders (to zero) when calling this method.
     *
     *
     * The gyroscope angle does not need to be reset here on the user's robot code.
     * The library automatically takes care of offsetting the gyro angle.
     *
     * @param pose The position on the field that your robot is at.
     * @param gyroAngle  The angle reported by the gyroscope.
     */
    fun resetPosition(pose: Pose2D, gyroAngle: Rotation2D) {
        this.pose = pose
        prevAngle = pose.rotation
        gyroOffset = pose.rotation - gyroAngle
        prevLeftDist = 0.0
        prevRightDist = 0.0
    }

    /**
     * Updates the robot position on the field using distance measurements from encoders. This
     * method is more numerically accurate than using velocities to integrate the pose and
     * is also advantageous for teams that are using lower CPR encoders.
     *
     * @param gyroAngle           The angle reported by the gyroscope.
     * @param leftDistance  The distance traveled by the left encoder.
     * @param rightDistance The distance traveled by the right encoder.
     * @return The new pose of the robot.
     */
    fun update(gyroAngle: Rotation2D, leftDistance: Double, rightDistance: Double): Pose2D {
        val deltaLeftDistance = leftDistance - prevLeftDist
        val deltaRightDistance = rightDistance - prevRightDist

        prevLeftDist = leftDistance
        prevRightDist = rightDistance

        val averageDeltaDistance = (deltaLeftDistance + deltaRightDistance) / 2.0
        val angle = gyroAngle.plus(gyroOffset)

        val newPose = pose + Twist2D(averageDeltaDistance, 0.0, (angle - prevAngle).radians()).exp()
        prevAngle = angle

        pose = Pose2D(newPose.translation, angle)
        return pose;
    }
}