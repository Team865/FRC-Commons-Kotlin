package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.trajectory.TrajectoryState

class Segment {
    var waypoints: List<Pose2D> = emptyList()
    var trajectory: List<TrajectoryState> = emptyList()

    var curvatureSum = 0.0
    var arcLength = 0.0
    var trajectoryTime = 0.0
    var maxK = 0.0
    var maxAngular = 0.0
    var maxAngularAcc = 0.0
}