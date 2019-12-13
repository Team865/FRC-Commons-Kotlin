package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.hypot

//Translation2D can often be used as mathematical vector.

@Suppress("MemberVisibilityCanBePrivate")
class Translation2D(val x: Double, val y: Double) {


    fun interpolate(other: Translation2D, x1: Double): Translation2D = when {
        x1 <= 0 -> this
        x1 >= 1 -> other
        else -> Translation2D(x1 * (other.x - x) + x, x1 * (other.y - y) + y)
    }

    fun transposed(): Translation2D {
        return Translation2D(y, x)
    }

    fun direction(): Rotation2D {
        return Rotation2D(x, y).unit()
    }

    fun rotate(by: Rotation2D): Translation2D {
        return Translation2D(x * by.cos - y * by.sin, x * by.sin + y * by.cos)
    }

    infix fun dot(other: Translation2D): Double {
        return x * other.x + y * other.y
    }

    infix fun cross(other: Translation2D): Double {
        return x * other.y - y * other.x
    }

    fun unit(): Translation2D {
        return scaled(1 / mag())
    }


    /**
     * Swaps the sign of the vector.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.inverseWorksProperly
     *
     * @return Translation2D object with a flipped sign.
     *
     */
    val inverse: Translation2D get() = Translation2D(-x, -y)


    /**
     * Tests to see if another vector is equal to this one.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.epsilonEqualsWorksProperly
     *
     * @return True or False
     *
     */
    fun epsilonEquals(state: Translation2D, epsilon: Double = 1E-12): Boolean =
            x.epsilonEquals(state.x, epsilon) && y.epsilonEquals(state.y, epsilon)

    /**
     * Adds one vector to another
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.plusWorksProperly
     *
     * @return The added vector.
     *
     */
    operator fun plus(by: Translation2D): Translation2D = Translation2D(x + by.x, y + by.y)

    /**
     * Subtracts one vector from another.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.minusWorksProperly
     *
     * @return The subtracted vector.
     *
     */
    operator fun minus(by: Translation2D): Translation2D = Translation2D(x - by.x, y - by.y)

    /**
     * Scales the vector by a double.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.scaledWorksProperly
     *
     * @return The scaled vector.
     *
     */
    fun scaled(by: Double): Translation2D {
        if (by == 1.0) {
            return this
        }
        return Translation2D(x * by, y * by)
    }

    /**
     * Multiplies the vector by a double.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.timesWorksProperly
     *
     * @return The scaled vector.
     *
     */
    operator fun times(by: Double): Translation2D = scaled(by)

    /**
     * Divides the vector by a double.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.divWorksProperly
     *
     * @return The scaled vector
     *
     */
    operator fun div(by: Double): Translation2D = scaled(1.0 / by)

    fun distanceTo(state: Translation2D): Double = hypot(state.x - x, state.y - y)

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
    fun mag(): Double = hypot(x, y)


    /**
     * Gets the X and Y of the vector in a string.
     *
     * **Example**
     *
     * @sample ca.warp7.frc.geometry.Translation2DTest.toStringWorksProperly
     *
     * @return String containing X and Y of the vector.
     *
     */
    override fun toString(): String {
        return "↘(${x.f}, ${y.f})"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Translation2D) return false
        return epsilonEquals(other)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        @JvmStatic
        val identity = Translation2D(0.0, 0.0)
    }
}
