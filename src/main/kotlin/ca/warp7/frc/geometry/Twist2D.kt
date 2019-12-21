package ca.warp7.frc.geometry

import ca.warp7.frc.f
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * <p>See <a href="https://file.tavsys.net/control/state-space-guide.pdf">
 * Controls Engineering in the FIRST Robotics Competition</a>
 * section on nonlinear pose estimation for derivation.
 *
 * <p>The twist is a change in pose in the robot's coordinate frame since the
 * previous pose update. When the user runs exp() on the previous known
 * field-relative pose with the argument being the twist, the user will
 * receive the new field-relative pose.
 *
 * <p>"Exp" represents the pose exponential, which is solving a differential
 * equation moving the pose forward in time.
 *
 * A Twist2D The change in pose in the robot's coordinate frame since the
 * previous pose update. For example, if a non-holonomic robot moves forward
 * 0.01 meters and changes angle by 0.5 degrees since the previous pose update,
 * the twist would be Twist2d{0.01, 0.0, toRadians(0.5)}
 */
@Suppress("MemberVisibilityCanBePrivate")
class Twist2D(val dx: Double, val dy: Double, val dTheta: Double) {
    override fun toString(): String {
        return "Twist(${dx.f}, ${dy.f}, ${dTheta.f})"
    }

    /**
     * Scale the twist transformation by a factor
     *
     * @param by the factor to scale
     */
    fun scaled(by: Double): Twist2D = Twist2D(dx * by, dy * by, dTheta * by)

    /**
     * @return the magnitude of the twist transformation
     */
    fun mag() = hypot(dx, dy)

    /**
     * Calculate the pose exponential
     */
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