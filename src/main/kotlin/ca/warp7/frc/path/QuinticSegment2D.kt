package ca.warp7.frc.path

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import kotlin.math.hypot

@Suppress("unused", "MemberVisibilityCanBePrivate", "CanBeParameter")
data class QuinticSegment2D(
        val x0: Double,
        val x1: Double,
        val dx0: Double,
        val dx1: Double,
        val ddx0: Double,
        val ddx1: Double,
        val y0: Double,
        val y1: Double,
        val dy0: Double,
        val dy1: Double,
        val ddy0: Double,
        val ddy1: Double
) {

    val x = QuinticSpline(x0, dx0, ddx0, x1, dx1, ddx1)
    val y = QuinticSpline(y0, dy0, ddy0, y1, dy1, ddy1)

    /**
     * Get the state without jerk so it's faster
     */
    fun getState(t: Double): Path2DState {
        return Path2DState(0.0, x.p(t), y.p(t), x.v(t), y.v(t), x.a(t), y.a(t), 0.0, 0.0)
    }

    /**
     * Get the pose without state so its faster
     */
    fun getPose(t: Double): Pose2D {
        val vx = x.v(t)
        val vy = y.v(t)
        val hp = hypot(vx, vy)
        return Pose2D(Translation2D(x.p(t), y.p(t)), Rotation2D(vx / hp, vy / hp))
    }

    operator fun get(t: Double): Path2DState {
        return Path2DState(t,  x.p(t), y.p(t), x.v(t), y.v(t), x.a(t), y.a(t), x.j(t), y.j(t))
    }
}