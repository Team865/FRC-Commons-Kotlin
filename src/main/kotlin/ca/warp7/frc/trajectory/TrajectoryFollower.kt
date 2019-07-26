package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.geometry.Pose2D

interface TrajectoryFollower {
    fun updateTrajectory(
            setpoint: TrajectoryState,
            error: Pose2D,
            velocity: ChassisState,
            acceleration: ChassisState
    )
}