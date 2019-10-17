package ca.warp7.frc.geometry

import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate")
class Pose2D(val translation: Translation2D, val rotation: Rotation2D) {

    constructor(x: Double, y: Double, rotation: Rotation2D) : this(Translation2D(x, y), rotation)

    fun epsilonEquals(state: Pose2D, epsilon: Double): Boolean =
            translation.epsilonEquals(state.translation, epsilon) && rotation.epsilonEquals(state.rotation, epsilon)

    fun epsilonEquals(state: Pose2D): Boolean = epsilonEquals(state, 1E-12)

    @Deprecated("", ReplaceWith("plus(by)"))
    fun transform(by: Pose2D): Pose2D = plus(by)

    operator fun plus(by: Pose2D): Pose2D =
            Pose2D(translation + by.translation.rotate(by.rotation), rotation + by.rotation)

    operator fun minus(by: Pose2D): Pose2D = plus(by.inverse)

    fun scaled(by: Double): Pose2D {
        if (by == 1.0) {
            return this
        }
        return Pose2D(translation.scaled(by), rotation.scaled(by))
    }

    operator fun times(by: Double): Pose2D = scaled(by)

    operator fun div(by: Double): Pose2D = scaled(1.0 / by)

    fun distanceTo(state: Pose2D): Double = (state - this).log().mag

    fun interpolate(other: Pose2D, x: Double): Pose2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> this + (other - this).log().scaled(x).exp()
    }

    val inverse: Pose2D get() = Pose2D(translation.rotate(rotation.inverse).inverse, rotation.inverse)

    override fun toString(): String {
        return "Pose($translation, $rotation)"
    }

    @Deprecated("log is now a function", ReplaceWith("log()"))
    val log: Twist2D
        get() = log()

    /**
     * Convert a Pose2D into a Twist2D transformation
     * By: Team 254
     */
    fun log(): Twist2D {
        val dTheta = rotation.radians
        val halfTheta = 0.5 * dTheta
        val cosMinusOne = rotation.cos - 1.0
        val halfThetaByTanOfHalfDTheta =
                if (abs(cosMinusOne) < 1E-9) 1.0 - 1.0 / 12.0 * dTheta * dTheta
                else -(halfTheta * rotation.sin) / cosMinusOne
        val delta = translation.rotate(Rotation2D(halfThetaByTanOfHalfDTheta, -halfTheta))
        return Twist2D(delta.x, delta.y, dTheta)
    }

    val mirrored: Pose2D
        get() = Pose2D(Translation2D(translation.x, -translation.y), rotation.inverse)

    companion object {
        val identity = Pose2D(Translation2D.identity, Rotation2D.identity)
    }
}