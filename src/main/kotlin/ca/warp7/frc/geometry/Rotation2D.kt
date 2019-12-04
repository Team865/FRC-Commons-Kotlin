package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.*

@Suppress("MemberVisibilityCanBePrivate")
class Rotation2D(val cos: Double, val sin: Double) {

    fun epsilonEquals(state: Rotation2D, epsilon: Double = 1E-12): Boolean =
            cos.epsilonEquals(state.cos, epsilon) && sin.epsilonEquals(state.sin, epsilon)

    operator fun plus(by: Rotation2D): Rotation2D =
            Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos)

    operator fun minus(by: Rotation2D): Rotation2D =
            Rotation2D(cos * by.cos - sin * -by.sin, cos * -by.sin + sin * by.cos)

    fun scaled(by: Double): Rotation2D {
        if (by == 1.0) {
            return this
        }
        return Rotation2D(cos * by, sin * by)
    }

    operator fun times(by: Double): Rotation2D = scaled(by)

    operator fun div(by: Double): Rotation2D = scaled(1.0 / by)

    fun distanceTo(state: Rotation2D): Double =
            atan2(y = cos * state.sin + -sin * state.cos, x = cos * state.cos - -sin * state.sin)

    fun interpolate(other: Rotation2D, x: Double): Rotation2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> {
            val angle = distanceTo(other) * x
            val c = cos(angle)
            val s = sin(angle)
            Rotation2D(cos * c - sin * s, cos * s + sin * c)
        }
    }

    val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    fun degrees(): Double {
        return Math.toDegrees(radians())
    }

    fun radians(): Double {
        return atan2(y = sin, x = cos)
    }

    fun tan(): Double = if (abs(cos) < 1E-12) {
        if (sin >= 0.0) {
            Double.POSITIVE_INFINITY
        } else {
            Double.NEGATIVE_INFINITY
        }
    } else sin / cos

    /**
     * Gets the magnitude of the vector.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.magWorksProperly
     *
     * @return the magnitude of the vector.
     *
     */
    fun mag(): Double = hypot(cos, sin)

    /**
     * Get the unit rotation vector
     */
    fun unit(): Rotation2D = scaled(1 / mag())

    override fun toString(): String {
        return "⟳${degrees().f}°"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rotation2D) return false
        return epsilonEquals(other)
    }

    override fun hashCode(): Int {
        var result = cos.hashCode()
        result = 31 * result + sin.hashCode()
        return result
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
