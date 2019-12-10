package ca.warp7.frc.geometry

/**
 * Rigid Transform (of a translation and a rotation)
 */
class Pose2D(val translation: Translation2D, val rotation: Rotation2D) {

    constructor(x: Double, y: Double, rotation: Rotation2D) : this(Translation2D(x, y), rotation)
    constructor(x: Double, y: Double, rotation: Double) : this(Translation2D(x, y), Rotation2D.fromRadians(rotation))

    fun epsilonEquals(state: Pose2D, epsilon: Double = 1E-12): Boolean =
            translation.epsilonEquals(state.translation, epsilon) && rotation.epsilonEquals(state.rotation, epsilon)

    operator fun plus(by: Pose2D): Pose2D =
            Pose2D(translation + by.translation.rotate(rotation), rotation + by.rotation)

    operator fun minus(by: Pose2D): Pose2D =
            Pose2D((translation - by.translation).rotate(by.rotation.inverse), rotation - by.rotation)

    fun scaled(by: Double): Pose2D {
        if (by == 1.0) {
            return this
        }
        return Pose2D(translation.scaled(by), rotation.scaled(by))
    }

    operator fun times(by: Double): Pose2D = scaled(by)

    operator fun div(by: Double): Pose2D = scaled(1.0 / by)

    fun interpolate(other: Pose2D, x: Double): Pose2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> this + (other - this).log().scaled(x).exp()
    }

    val inverse: Pose2D get() = Pose2D(translation.rotate(rotation.inverse).inverse, rotation.inverse)

    override fun toString(): String {
        return "Pose($translation, $rotation)"
    }

    /**
     * Convert a Pose2D into a Twist2D transformation
     */
    fun log(): Twist2D {
        val dTheta = rotation.radians()
        val halfThetaByTanOfHalfDTheta =
                if (1.0 - rotation.cos < 1E-9) 1.0 - 1.0 / 12.0 * dTheta * dTheta
                else (0.5 * dTheta) * rotation.sin / (1.0 - rotation.cos)
        return Twist2D(
                translation.x * halfThetaByTanOfHalfDTheta + translation.y * dTheta / 2.0,
                translation.y * halfThetaByTanOfHalfDTheta - translation.x * dTheta / 2.0,
                dTheta
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Pose2D) return false
        return epsilonEquals(other)
    }

    override fun hashCode(): Int {
        var result = translation.hashCode()
        result = 31 * result + rotation.hashCode()
        return result
    }

    companion object {
        @JvmStatic
        val identity = Pose2D(Translation2D.identity, Rotation2D.identity)
    }
}