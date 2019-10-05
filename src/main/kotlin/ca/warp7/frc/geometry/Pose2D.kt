package ca.warp7.frc.geometry

import kotlin.math.abs

class Pose2D(val translation: Translation2D, val rotation: Rotation2D) {

    constructor(x: Double, y: Double, rotation: Rotation2D) : this(Translation2D(x, y), rotation)

    operator fun unaryMinus(): Pose2D = inverse

    operator fun unaryPlus(): Pose2D = copy

    val copy: Pose2D get() = Pose2D(translation.copy, rotation.copy)

    val isIdentity: Boolean
        get() = epsilonEquals(identity)

    fun epsilonEquals(state: Pose2D, epsilon: Double): Boolean =
            translation.epsilonEquals(state.translation, epsilon) && rotation.epsilonEquals(state.rotation, epsilon)

    fun epsilonEquals(state: Pose2D): Boolean = epsilonEquals(state, 1E-12)

    fun transform(by: Pose2D): Pose2D =
            Pose2D(translation.transform(by.translation.rotate(by.rotation)), rotation.transform(by.rotation))

    operator fun plus(by: Pose2D): Pose2D = transform(by)

    operator fun minus(by: Pose2D): Pose2D = transform(by.inverse)

    fun scaled(by: Double): Pose2D = Pose2D(translation.scaled(by), rotation.scaled(by))

    operator fun times(by: Double): Pose2D = scaled(by)

    operator fun div(by: Double): Pose2D = scaled(1.0 / by)

    fun distanceTo(state: Pose2D): Double = (state - this).log.mag

    val state: Pose2D get() = this

    @Deprecated("")
    operator fun rangeTo(state: Pose2D): Interpolator<Pose2D> =
            object : Interpolator<Pose2D> {
                override fun get(x: Double): Pose2D = interpolate(state, x)
            }

    fun interpolate(other: Pose2D, x: Double): Pose2D = when {
        x <= 0 -> copy
        x >= 1 -> other.copy
        else -> transform((other - this).log.scaled(x).exp)
    }

    val inverse: Pose2D get() = Pose2D(-translation.rotate(-rotation), -rotation)

    override fun toString(): String {
        return "Pose($translation, $rotation)"
    }

    /**
     * Convert this into a Twist2D transformation
     * By: Team 254
     */
    val log: Twist2D
        get() {
            val dTheta = rotation.radians
            val halfTheta = 0.5 * dTheta
            val cosMinusOne = rotation.cos - 1.0
            val halfThetaByTanOfHalfDTheta =
                    if (abs(cosMinusOne) < 1E-9) 1.0 - 1.0 / 12.0 * dTheta * dTheta
                    else -(halfTheta * rotation.sin) / cosMinusOne
            val translation = translation.rotate(Rotation2D(halfThetaByTanOfHalfDTheta, -halfTheta))
            return Twist2D(translation.x, translation.y, dTheta)
        }

    @Suppress("unused")
    val mirrored: Pose2D
        get() = Pose2D(Translation2D(translation.x, -translation.y), rotation.inverse)

    companion object {
        val identity = Pose2D(Translation2D.identity, Rotation2D.identity)
    }
}