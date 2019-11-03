package ca.warp7.frc.path

import kotlin.math.pow

class CubicSpline(
        p0: Double,
        m0: Double,
        p1: Double,
        m1: Double
) {

    private val a: Double = 2 * p0 - 2 * p1 + m0 + m1
    private val b: Double = -2 * m0 - m1 - 3 * p0 + 3 * p1
    private val c: Double = m0
    private val d: Double = p0

    fun p(t: Double) = a * t.pow(3) + b * t.pow(2) + c * t + d
    fun v(t: Double) = 3 * a * t.pow(2) + 2 * b * t + c
    fun a(t: Double) = 6 * a * t + 2 * b
    fun j() = 6 * a
}