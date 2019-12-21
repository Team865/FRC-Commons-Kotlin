package ca.warp7.frc.geometry

/**
 * Rigid Transform (of a translation and a rotation)
 *
 * Can be used to describe a direction and heading (pose) relative
 * to another point, or the relative transform between two poses
 *
 * @param translation The translational component of the pose.
 * @param rotation    The rotational component of the pose.
 */
class Pose2D(val translation: Translation2D, val rotation: Rotation2D) {

    /**
     * Convenience constructors that takes in x and y values directly instead of
     * having to construct a Translation2D.
     *
     * @param x        The x component of the translational component of the pose.
     * @param y        The y component of the translational component of the pose.
     * @param rotation The rotational component of the pose.
     */
    constructor(x: Double, y: Double, rotation: Rotation2D) : this(Translation2D(x, y), rotation)

    /**
     * Convenience constructors that takes in x, y, and angle directly instead of
     * having to construct a Translation2D and a Rotation2D
     *
     * @param x        The x component of the translational component of the pose.
     * @param y        The y component of the translational component of the pose.
     * @param rotation The rotational component of the pose.
     */
    constructor(x: Double, y: Double, rotation: Double) : this(Translation2D(x, y), Rotation2D.fromRadians(rotation))

    fun epsilonEquals(state: Pose2D, epsilon: Double = 1E-12): Boolean =
            translation.epsilonEquals(state.translation, epsilon) && rotation.epsilonEquals(state.rotation, epsilon)

    /**
     * Transforms the pose by the given transformation and returns the new
     * transformed pose.
     *
     * <p>The matrix multiplication is as follows
     * [x_new,]    [cos, -sin, 0][transform.x,]
     * [y_new,] += [sin,  cos, 0][transform.y,]
     * [t_new,]    [0,    0,   1][transform.t,]
     *
     * @param by The transform to transform the pose by.
     * @return The transformed pose.
     */
    operator fun plus(by: Pose2D): Pose2D =
            Pose2D(translation + by.translation.rotate(rotation), rotation + by.rotation)

    /**
     * Returns the Transform2d that maps the one pose to another.
     *
     * This function can often be used for trajectory tracking or pose
     * stabilization algorithms to get the error between the reference and the
     * current pose.
     *
     * @param by The initial pose of the transformation.
     * @return The transform that maps the other pose to the current pose.
     */
    operator fun minus(by: Pose2D): Pose2D =
            Pose2D((translation - by.translation).rotate(by.rotation.inverse), rotation - by.rotation)


    /**
     * Interpolate between this pose and other pose
     *
     * @param other the end pose
     * @param x the interpolating value between 0 and 1
     *
     * @return the interpolated Pose2D
     */
    fun interpolate(other: Pose2D, x: Double): Pose2D = when {
        x <= 0 -> this
        x >= 1 -> other
        else -> this + (other - this).log().scaled(x).exp()
    }

    /**
     * Returns the inverse transformation of this pose
     */
    val inverse: Pose2D get() = Pose2D(translation.rotate(rotation.inverse).inverse, rotation.inverse)

    override fun toString(): String {
        return "Pose($translation, $rotation)"
    }

    /**
     * Returns a Twist2d that maps this pose to the end pose. If c is the output
     * of a.Log(b), then a.Exp(c) would yield b.
     *
     * @return The twist that maps this to end.
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