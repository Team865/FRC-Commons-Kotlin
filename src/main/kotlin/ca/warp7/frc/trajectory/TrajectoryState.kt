package ca.warp7.frc.trajectory

import ca.warp7.frc.f
import ca.warp7.frc.geometry.Pose2D

/**
 * Defines a trajectory state, which is a point on a curved
 * path with velocity information
 */
class TrajectoryState(
        val pose: Pose2D,
        val curvature: Double
) {

    var v: Double = 0.0
    var w: Double = 0.0
    var dv: Double = 0.0
    var dw: Double = 0.0
    var ddv: Double = 0.0
    var ddw: Double = 0.0
    var t: Double = 0.0

    override fun toString(): String {
        return "(t=${t.f}, $pose, k=${curvature.f}, v=${v.f}, ω=${w.f}, a=${dv.f}, dω=${dw.f}, j=${ddv.f}, ddω=${ddw.f})"
    }
}