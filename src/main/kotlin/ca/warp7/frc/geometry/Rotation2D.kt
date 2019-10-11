package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f

@Suppress("MemberVisibilityCanBePrivate")
class Rotation2D(val cos: Double, val sin: Double) {

    operator fun unaryMinus(): Rotation2D = inverse

    fun epsilonEquals(state: Rotation2D, epsilon: Double): Boolean =
            cos.epsilonEquals(state.cos, epsilon) && sin.epsilonEquals(state.sin, epsilon)

    fun epsilonEquals(state: Rotation2D): Boolean = epsilonEquals(state, 1E-12)

    fun transform(by: Rotation2D): Rotation2D =
            Rotation2D(cos * by.cos - sin * by.sin, cos * by.sin + sin * by.cos).norm

    operator fun plus(by: Rotation2D): Rotation2D = transform(by)

    operator fun minus(by: Rotation2D): Rotation2D = transform(by.inverse)

    fun scaled(by: Double): Rotation2D = Rotation2D(cos * by, sin * by)

    operator fun times(by: Double): Rotation2D = scaled(by)

    operator fun div(by: Double): Rotation2D = scaled(1.0 / by)

    fun distanceTo(state: Rotation2D): Double = (state - this).radians

    fun interpolate(other: Rotation2D, x: Double): Rotation2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> transform(Rotation2D.fromRadians(radians = distanceTo(other) * x))
    }

    val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    override fun toString(): String {
        return "⟳${degrees.f}°"
    }

    companion object {
        val identity = Rotation2D(1.0, 0.0)
    }
}