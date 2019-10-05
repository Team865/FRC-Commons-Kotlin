package ca.warp7.frc

import kotlin.math.abs
import kotlin.math.sign

// Commons.kt --- common constants and functions

const val kFeetToMetres: Double = 0.3048

const val kInchesToMetres: Double = 0.0254

const val kMetresToFeet: Double = 1 / kFeetToMetres

const val kMetresToInches: Double = 1 / kInchesToMetres

/**
 * Check if a number is close enough to another by [epsilon]
 */
fun Double.epsilonEquals(other: Double, epsilon: Double) =
        (this - epsilon <= other) && (this + epsilon >= other)

/**
 * Check if a number is close enough to another by 1E-12
 */
fun Double.epsilonEquals(other: Double) = epsilonEquals(other, 1E-12)

/**
 * Interpolate between two numbers
 *
 * This function is undefined if any of its parameters are
 * Infinity or NaN
 */
fun linearInterpolate(a: Double, b: Double, x: Double): Double {
    if (x < 0.0) {
        return a
    }
    if (x > 1.0) {
        return b
    }
    return a + (b - a) * x
}


/**
 * Limits a value
 *
 * @param max the maximum magnitude of the value. Must be positive
 */
fun Double.limit(max: Double): Double {
    if (this > max) {
        return max
    }
    if (this < -max) {
        return -max
    }
    return this
}


/**
 * Applies a deadband to a value
 */
fun applyDeadband(value: Double, max: Double, deadband: Double): Double {
    val v = value.limit(max)
    return if (abs(v) > deadband) {
        if (v > 0.0) {
            (v - deadband) / (max - deadband)
        } else {
            (v + deadband) / (max - deadband)
        }
    } else {
        0.0
    }
}


/**
 * Format a number to 3 decimal places
 */
val Double.f get() = "%.3f".format(this)

/**
 * Format a number to 1 decimal places
 */
val Double.f1 get() = "%.1f".format(this)

/**
 * Converts a number in feet into metres
 */
val Number.feet: Double get() = this.toDouble() * kFeetToMetres

/**
 * Converts a number in inches into metres
 */
val Number.inches: Double get() = this.toDouble() * kInchesToMetres

/**
 * Squares a number
 */
val Double.squared: Double get() = this * this

/**
 * Squares a number and keep the sign
 */
val Double.squaredWithSign: Double get() = this * this * sign

/**
 * Create an integer sign representation of a boolean
 */
fun Boolean.toSign() = if (this) 1 else -1

/**
 * Create a double representation of a boolean
 */
fun Boolean.toDouble() = if (this) 1.0 else 0.0

/**
 * Create an integer representation of a boolean
 */
fun Boolean.toInt() = if (this) 1 else 0