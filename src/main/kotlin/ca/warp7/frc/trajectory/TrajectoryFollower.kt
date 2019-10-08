package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D

interface TrajectoryFollower {
    fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    )
}