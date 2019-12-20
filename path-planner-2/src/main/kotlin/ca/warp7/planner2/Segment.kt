package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.trajectory.TrajectoryState

class Segment {
    var inverted = false
    var waypoints: List<Pose2D> = emptyList()
    var trajectory: List<TrajectoryState> = emptyList()

    var curvatureSum = 0.0
    var arcLength = 0.0
    var trajectoryTime = 0.0
    var maxCurvature = 0.0
    var maxAngular = 0.0
    var maxAngularAcc = 0.0

    /**
     * Get an interpolated view
     *
     * Assumes that points are close enough that everything is linear
     */
    fun sample(t: Double): TrajectoryState {
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]
        val x = if (last.t == next.t) 1.0 else (t - last.t) / (next.t - last.t)

        val curvature = linearInterpolate(last.curvature, next.curvature, x)
        val position = last.pose.translation.interpolate(next.pose.translation, x)
        val heading = last.pose.rotation.interpolate(next.pose.rotation, x)

        val state = TrajectoryState(Pose2D(position, heading), curvature)

        state.v = linearInterpolate(last.v, next.v, x)
        state.dv = linearInterpolate(last.dv, next.dv, x)
        state.w = linearInterpolate(last.w, next.w, x)
        state.dw = linearInterpolate(last.dw, next.dw, x)
        state.t = t

        return if (inverted) state.inverted() else state
    }
}