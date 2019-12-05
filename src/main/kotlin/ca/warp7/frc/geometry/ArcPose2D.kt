package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f

/**
 * Represents a pose section on a curved path
 */
@Suppress("MemberVisibilityCanBePrivate")
class ArcPose2D(
        val pose: Pose2D,
        val curvature: Double,
        val dk_ds: Double
) {
    constructor(pose: Pose2D) : this(pose, 0.0, 0.0)
    constructor(pose: Pose2D, curvature: Double) : this(pose, curvature, 0.0)

    val translation: Translation2D = pose.translation
    val rotation: Rotation2D = pose.rotation

    fun epsilonEquals(state: ArcPose2D, epsilon: Double = 1E-12): Boolean =
            pose.epsilonEquals(state.pose, epsilon) &&
                    curvature.epsilonEquals(state.curvature, epsilon) &&
                    dk_ds.epsilonEquals(state.dk_ds, epsilon)

    override fun toString(): String {
        return "Arc($pose, k=${curvature.f})"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ArcPose2D) return false
        return epsilonEquals(other)
    }


    override fun hashCode(): Int {
        var result = pose.hashCode()
        result = 31 * result + curvature.hashCode()
        result = 31 * result + dk_ds.hashCode()
        return result
    }
}