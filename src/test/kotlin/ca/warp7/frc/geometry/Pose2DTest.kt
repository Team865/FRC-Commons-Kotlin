package ca.warp7.frc.geometry

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class Pose2DTest {
    private val kEpsilon = 1E-9

    @Test
    fun testTransformBy() {
        val initial = Pose2D(Translation2D(1.0, 2.0), Rotation2D.fromDegrees(45.0))
        val transformation = Pose2D(Translation2D(5.0, 0.0), Rotation2D.fromDegrees(5.0))

        val transformed = initial + transformation

        assertEquals(transformed.translation.x, 1 + 5.0 / sqrt(2.0), kEpsilon)
        assertEquals(transformed.translation.y, 2 + 5.0 / sqrt(2.0), kEpsilon)
        assertEquals(transformed.rotation.toDegrees(), 50.0, kEpsilon)
    }

    @Test
    fun testRelativeTo() {
        val initial = Pose2D(0.0, 0.0, Rotation2D.fromDegrees(45.0))
        val last = Pose2D(5.0, 5.0, Rotation2D.fromDegrees(45.0))

//        val finalRelativeToInitial = last.relativeTo(initial)
        val finalRelativeToInitial = last - initial


        assertEquals( 5.0 * sqrt(2.0), finalRelativeToInitial.translation.x, kEpsilon)
        assertEquals(0.0, finalRelativeToInitial.translation.y, kEpsilon)
        assertEquals(0.0, finalRelativeToInitial.rotation.toDegrees(), kEpsilon)
    }

    @Test
    fun testEquality() {
        val one = Pose2D(0.0, 5.0, Rotation2D.fromDegrees(43.0))
        val two = Pose2D(0.0, 5.0, Rotation2D.fromDegrees(43.0))
        assertEquals(one, two)
    }

    @Test
    fun testInequality() {
        val one = Pose2D(0.0, 5.0, Rotation2D.fromDegrees(43.0))
        val two = Pose2D(0.0, 1.524, Rotation2D.fromDegrees(43.0))
        assertNotEquals(one, two)
    }
}