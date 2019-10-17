package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.hypot

//Translation2D can often be used as mathimatical vector.

@Suppress("MemberVisibilityCanBePrivate")
class Translation2D(val x: Double, val y: Double) {


    fun interpolate(other: Translation2D, x1: Double): Translation2D = when {
        x1 <= 0 -> this
        x1 >= 1 -> other
        else -> Translation2D(x1 * (other.x - x) + x, x1 * (other.y - y) + y)
    }

    /**
     * Swaps the sign of the vector.
     *
     * **Example**
     *
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.inverseWorksProperly
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
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.epsilonEqualsWorksProperly
     *
     * @return True or False
     *
     */
    fun epsilonEquals(state: Translation2D, epsilon: Double = 1E-12): Boolean =
            x.epsilonEquals(state.x, epsilon) && y.epsilonEquals(state.y, epsilon)


    /**
     * Adds the X and Y of the vector to the X and Y of the input vector.
     *
     * **Example**
     *
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.transformWorksProperly
     *
     * @return The transformed vector.
     *
     */
    @Deprecated("", ReplaceWith("this + by"))
    fun transform(by: Translation2D): Translation2D = this + by


    /**
     * Adds one vector to another
     *
     * **Example**
     *
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.plusWorksProperly
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
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.minusWorksProperly
     *
     * @return The subtracted vector.
     *
     */
    operator fun minus(by: Translation2D): Translation2D = Translation2D(x - by.x, y - by.y)

    /**
     * Gets the X and Y of the vector in a string.
     *
     * **Example**
     *
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.toStringWorksProperly
     *
     * @return String containing X and Y of the vector.
     *
     */
    override fun toString(): String {
        return "↘(${x.f}, ${y.f})"
    }

    /**
     * Scales the vector by a double.
     *
     * **Example**
     *
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.scaledWorksProperly
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
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.timesWorksProperly
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
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.divWorksProperly
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
     * @sample test.ca.warp7.frc.geometry.Translation2DTest.magWorksProperly
     *
     * @return the magnitude of the vector.
     *
     */
    val mag: Double get() = hypot(x, y)

    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}
