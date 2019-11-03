package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Suppress("MemberVisibilityCanBePrivate")
class Rotation2D(val cos: Double, val sin: Double) {

    fun epsilonEquals(state: Rotation2D, epsilon: Double = 1E-12): Boolean =
            cos.epsilonEquals(state.cos, epsilon) && sin.epsilonEquals(state.sin, epsilon)

    operator fun plus(by: Rotation2D): Rotation2D =
            Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos).norm

    operator fun minus(by: Rotation2D): Rotation2D =
            Rotation2D(cos * by.cos + sin * by.sin, sin * by.cos - cos * by.sin).norm

    fun scaled(by: Double): Rotation2D {
        if (by == 1.0) {
            return this
        }
        return Rotation2D(cos * by, sin * by)
    }

    operator fun times(by: Double): Rotation2D = scaled(by)

    operator fun div(by: Double): Rotation2D = scaled(1.0 / by)

    fun distanceTo(state: Rotation2D): Double =
            atan2(cos * state.cos + sin * state.sin, cos * state.sin - sin * state.cos)

    fun interpolate(other: Rotation2D, x: Double): Rotation2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> this + fromRadians(radians = distanceTo(other) * x)
    }

    val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    override fun toString(): String {
        return "⟳${degrees.f}°"
    }

    companion object {

        /**
         * Creates a [Rotation2D] object from the angle in radians
         */
        @JvmStatic
        fun fromRadians(radians: Double): Rotation2D = Rotation2D(cos(radians), sin(radians))

        /**
         * Creates a [Rotation2D] object from the angle in degrees
         */
        @JvmStatic
        fun fromDegrees(degrees: Double) = fromRadians(Math.toRadians(degrees))

        /**
         * The [Rotation2D] in which when transformed onto another [Rotation2D]
         * does not change its value
         */
        @JvmStatic
        val identity = Rotation2D(1.0, 0.0)
    }
}
