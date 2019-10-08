package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D

data class TrajectorySegment(
        val waypoints: MutableList<Pose2D>,
        val wheelbaseRadius: Double, // m
        val maxVelocity: Double, // m/s
        val maxAcceleration: Double, // m/s^2
        val maxCentripetalAcceleration: Double, // s^-1
        val maxJerk: Double, // m/s^3
        val inverted: Boolean,
        val mirrored: Boolean,
        val follower: TrajectoryFollower
)