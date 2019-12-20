package ca.warp7.frc.trajectory

import ca.warp7.frc.f
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D

/**
 * Defines a trajectory state, which is a point on a curved
 * path with velocity information
 */
class TrajectoryState(val pose: Pose2D, val curvature: Double) {

    @JvmField var v = 0.0
    @JvmField var w = 0.0
    @JvmField var dv = 0.0
    @JvmField var dw = 0.0
    @JvmField var ddv = 0.0
    @JvmField var ddw = 0.0
    @JvmField var t = 0.0

    override fun toString(): String {
        return "(t=${t.f}, $pose, k=${curvature.f}, v=${v.f}, ω=${w.f}, " +
                "a=${dv.f}, dω=${dw.f}, j=${ddv.f}, ddω=${ddw.f})"
    }

    fun inverted(): TrajectoryState {
        val h = pose.rotation
        val s = TrajectoryState(Pose2D(pose.translation, Rotation2D(-h.cos, -h.sin)), -curvature)
        s.v = -v
        s.dv = -dv
        s.ddv = -ddv
        s.w = w
        s.dw = dw
        s.ddw = ddw
        s.t = t
        return s
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrajectoryState) return false
        return pose == other.pose
    }

    override fun hashCode(): Int {
        return pose.hashCode()
    }
}