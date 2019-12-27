package ca.warp7.frc.geometry

import ca.warp7.frc.geometry.Rotation2D.Companion.fromDegrees
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class Twist2DTest {
    @Test
    fun testStraightLineTwist() {
        val straight = Twist2D(5.0, 0.0, 0.0)
        val straightPose = straight.exp()
        assertEquals(straightPose.translation.x, 5.0, kEpsilon)
        assertEquals(straightPose.translation.y, 0.0, kEpsilon)
        assertEquals(straightPose.rotation.radians(), 0.0, kEpsilon)

    }

    @Test
    fun testQuarterCirleTwist() {
        val quarterCircle = Twist2D(5.0 / 2.0 * Math.PI, 0.0, Math.PI / 2.0)
        val quarterCirclePose = quarterCircle.exp()
        assertEquals(quarterCirclePose.translation.x, 5.0, kEpsilon)
        assertEquals(quarterCirclePose.translation.y, 5.0, kEpsilon)
        assertEquals(quarterCirclePose.rotation.degrees(), 90.0, kEpsilon)

    }

    @Test
    fun testDiagonalNoDtheta() {
        val diagonal = Twist2D(2.0, 2.0, 0.0)
        val diagonalPose = diagonal.exp()
        assertEquals(diagonalPose.translation.x, 2.0, kEpsilon)
        assertEquals(diagonalPose.translation.y, 2.0, kEpsilon)
        assertEquals(diagonalPose.rotation.degrees(), 0.0, kEpsilon)
    }

    @Test
    fun testEquality() {
        val one = Twist2D(5.0, 1.0, 3.0)
        val two = Twist2D(5.0, 1.0, 3.0)
        assertEquals(one, two)
    }

    @Test
    fun testInequality() {
        val one = Twist2D(5.0, 1.0, 3.0)
        val two = Twist2D(5.0, 1.2, 3.0)
        assertNotEquals(one, two)
    }

    @Test
    fun testPose2DLog() {
        val start = Pose2D()
        val end = Pose2D(5.0, 5.0, fromDegrees(90.0))
        val twist = end.minus(start).log()
        assertEquals(twist.dx, 5.0 / 2.0 * Math.PI, kEpsilon)
        assertEquals(twist.dy, 0.0, kEpsilon)
        assertEquals(twist.dTheta, Math.PI / 2.0, kEpsilon)
    }

    companion object {
        private const val kEpsilon = 1E-9
    }
}