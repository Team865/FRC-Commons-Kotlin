package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import ca.warp7.frc.linearInterpolate
import kotlin.math.atan2

/**
 * Represents a pose section on a curved path
 */
@Suppress("MemberVisibilityCanBePrivate")
class ArcPose2D(
        val pose: Pose2D,
        val curvature: Double,
        val dk_ds: Double
) {

    val translation: Translation2D = pose.translation
    val rotation: Rotation2D = pose.rotation

    fun epsilonEquals(state: ArcPose2D, epsilon: Double = 1E-12): Boolean =
            pose.epsilonEquals(state.pose, epsilon) &&
                    curvature.epsilonEquals(state.curvature, epsilon) &&
                    dk_ds.epsilonEquals(state.dk_ds, epsilon)

    @Deprecated("", ReplaceWith("this + by"))
    fun transform(by: ArcPose2D): ArcPose2D = this + by

    operator fun plus(by: ArcPose2D): ArcPose2D = ArcPose2D(pose + by.pose, curvature, dk_ds)

    operator fun minus(by: ArcPose2D): ArcPose2D = ArcPose2D(pose - by.pose, curvature, dk_ds)

    fun scaled(by: Double): ArcPose2D {
        if (by == 1.0) {
            return this
        }
        return ArcPose2D(pose.scaled(by), curvature, dk_ds)
    }

    operator fun times(by: Double): ArcPose2D = scaled(by)

    operator fun div(by: Double): ArcPose2D = scaled(1.0 / by)

    fun distanceTo(state: ArcPose2D): Double = pose.distanceTo(state.pose)

    fun interpolate(other: ArcPose2D, x: Double): ArcPose2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> ArcPose2D(
                pose.interpolate(other.pose, x),
                linearInterpolate(curvature, other.curvature, x),
                linearInterpolate(dk_ds, other.dk_ds, x)
        )
    }

    override fun toString(): String {
        return "Arc($pose, k=${curvature.f})"
    }

    companion object {
        val identity = ArcPose2D(Pose2D.identity, 0.0, 0.0)
    }
}