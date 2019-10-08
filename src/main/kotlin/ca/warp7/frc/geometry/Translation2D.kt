package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import kotlin.math.hypot

//Translation2D can often be used as mathimatical vector.

@Suppress("MemberVisibilityCanBePrivate")
class Translation2D(val x: Double, val y: Double) {

    val state: Translation2D get() = this

    @Deprecated("")
    operator fun rangeTo(state: Translation2D): Interpolator<Translation2D> =
            object : Interpolator<Translation2D> {
        override fun get(x: Double) = interpolate(state, x)
    }

    fun interpolate(other: Translation2D, x: Double): Translation2D = when {
        x <= 0 -> copy
        x >= 1 -> other.copy
        else -> Translation2D(x * (other.x - this.x) + this.x, x * (other.y - y) + y)
    }

    operator fun unaryMinus(): Translation2D = inverse

    /**
    * Swaps the sign of the vector.
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2D.inverseWorksProperly
    *
    * @return Translation2D object with a flipped sign.
    *
    */
    val inverse: Translation2D get() = Translation2D(-x, -y)

    operator fun unaryPlus(): Translation2D = copy

    val copy: Translation2D get() = Translation2D(x, y)

    val isIdentity: Boolean get() = epsilonEquals(identity)

    fun epsilonEquals(state: Translation2D, epsilon: Double): Boolean =
            x.epsilonEquals(state.x, epsilon) && y.epsilonEquals(state.y, epsilon)

    /**
    * Tests to see if another vector is equal to this one.
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2D.epsilonEqualsWorksProperly
    *
    * @return True or False
    *
    */
    fun epsilonEquals(state: Translation2D): Boolean = epsilonEquals(state, 1E-12)


    /**
    * Adds the X and Y of the vector to the X and Y of the input vector.
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2D.transformWorksProperly
    *
    * @return The transformed vector.
    *
    */
    fun transform(by: Translation2D): Translation2D {
        return Translation2D(x + by.x, y + by.y)
    }


    /**
    * Adds one vector to another
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2D.plusWorksProperly
    *
    * @return The added vector.
    *
    */
    operator fun plus(by: Translation2D): Translation2D = transform(by)

    /**
    * Subtracts one vector from another.
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2D.minusWorksProperly
    *
    * @return The subtracted vector.
    *
    */
    operator fun minus(by: Translation2D): Translation2D = transform(by.inverse)

    /**
    * Gets the X and Y of the vector in a string.
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2D.toStringWorksProperly
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
    * @return None, changes the vector.
    *
    */
    fun scaled(by: Double): Translation2D = Translation2D(x * by, y * by)

    /**
    * Multiplys the vector by a double.
    *
    * **Example**
    *
    * @sample test.ca.warp7.frc.geometry.Translation2DTest.timesWorksProperly
    * 
    * @return None, changes the vector.
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
    * @return None, changes the vector.
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
