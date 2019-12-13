package ca.warp7.frc.path

class QuinticSpline(
        p0: Double,
        m0: Double,
        dm0: Double,
        p1: Double,
        m1: Double,
        dm1: Double
) {

    private val a: Double = -6 * p0 - 3 * m0 - 0.5 * dm0 + 0.5 * dm1 - 3 * m1 + 6 * p1
    private val b: Double = 15 * p0 + 8 * m0 + 1.5 * dm0 - dm1 + 7 * m1 - 15 * p1
    private val c: Double = -10 * p0 - 6 * m0 - 1.5 * dm0 + 0.5 * dm1 - 4 * m1 + 10 * p1
    private val d: Double = 0.5 * dm0
    private val e: Double = m0
    private val f: Double = p0

    fun p(t: Double) = a * t * t * t * t * t + b * t * t * t * t + c * t * t * t + d * t * t + e * t + f
    fun v(t: Double) = 5 * a * t * t * t * t + 4 * b * t * t * t + 3 * c * t * t + 2 * d * t + e
    fun a(t: Double) = 20 * a * t * t * t + 12 * b * t * t + 6 * c * t + 2 * d
    fun j(t: Double) = 60 * a * t * t + 24 * b * t + 6 * c
}