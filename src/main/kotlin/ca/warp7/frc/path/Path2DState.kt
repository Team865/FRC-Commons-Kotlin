package ca.warp7.frc.path

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

data class Path2DState(
        val t: Double,
        val px: Double,
        val py: Double,
        val vx: Double,
        val vy: Double,
        val ax: Double,
        val ay: Double,
        val jx: Double,
        val jy: Double
) {
    fun curvature() = (vx * ay - ax * vy) / (vx * vx + vy * vy).pow(1.5)

    fun point() = Translation2D(px, py)

    fun heading(): Rotation2D {
        val mag = hypot(vx, vy)
        return Rotation2D(vx / mag, vy / mag)
    }

    fun pose() = Pose2D(point(), heading())

    fun dCurvature(): Double {
        val dx2dy2 = vx.pow(2) + vy.pow(2)
        val num = (vx * jy - jx * vy) * dx2dy2 - 3.0 * (vx * ay - ax * vy) * (vx * ax + vy * ay)
        return num / (dx2dy2 * dx2dy2 * sqrt(dx2dy2))
    }

    @Suppress("PropertyName")
    fun dCurvature_dS(): Double = dCurvature() / hypot(vx, vy)

    fun dCurvatureSquared(): Double {
        val dx2dy2 = vx.pow(2) + vy.pow(2)
        val num = (vx * jy - jx * vy) * dx2dy2 - 3.0 * (vx * ay - ax * vy) * (vx * ax + vy * ay)
        return num.pow(2) / dx2dy2.pow(5)
    }
}