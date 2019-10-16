package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.atan2

@Suppress("MemberVisibilityCanBePrivate")
class Rotation2D(val cos: Double, val sin: Double) {

    @Deprecated("", ReplaceWith("inverse"))
    operator fun unaryMinus(): Rotation2D = inverse

    fun epsilonEquals(state: Rotation2D, epsilon: Double = 1E-12): Boolean =
            cos.epsilonEquals(state.cos, epsilon) && sin.epsilonEquals(state.sin, epsilon)

    @Deprecated("", ReplaceWith("this + by"))
    fun transform(by: Rotation2D): Rotation2D = this + by

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
        else -> this + Rotation2D.fromRadians(radians = distanceTo(other) * x)
    }

    val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    override fun toString(): String {
        return "⟳${degrees.f}°"
    }

    companion object {
        val identity = Rotation2D(1.0, 0.0)
    }
}