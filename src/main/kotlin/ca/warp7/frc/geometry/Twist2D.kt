package ca.warp7.frc.geometry

import ca.warp7.frc.f
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Delta between two Pose2D transformations
 */
@Suppress("MemberVisibilityCanBePrivate")
class Twist2D(val dx: Double, val dy: Double, val dTheta: Double) {
    override fun toString(): String {
        return "Twist(${dx.f}, ${dy.f}, ${dTheta.f})"
    }

    fun scaled(by: Double): Twist2D = Twist2D(dx * by, dy * by, dTheta * by)

    fun mag() = hypot(dx, dy)

    fun exp(): Pose2D {
        val sinTheta = sin(dTheta)
        val cosTheta = cos(dTheta)
        val s: Double
        val c: Double
        if (abs(dTheta) < 1E-9) {
            s = 1.0 - 1.0 / 6.0 * dTheta * dTheta
            c = 0.5 * dTheta
        } else {
            s = sinTheta / dTheta
            c = (1.0 - cosTheta) / dTheta
        }
        return Pose2D(Translation2D(dx * s - dy * c, dx * c + dy * s), Rotation2D(cosTheta, sinTheta))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Twist2D) return false
        return dx == other.dx && dy == other.dy && dTheta == other.dTheta
    }

    override fun hashCode(): Int {
        var result = dx.hashCode()
        result = 31 * result + dy.hashCode()
        result = 31 * result + dTheta.hashCode()
        return result
    }
}