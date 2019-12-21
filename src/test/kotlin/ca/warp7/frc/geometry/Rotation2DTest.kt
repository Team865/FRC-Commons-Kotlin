package ca.warp7.frc.geometry

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

//This is testing the Rotation2D class using unit testing

class Rotation2DTest {

    @Test
    fun inverseWorksProperly() {
        val rotateTest = Rotation2D(0.9, 0.05).inverse
        val rotateGolden = Rotation2D(0.9, -0.05)
        assertTrue(rotateTest.epsilonEquals(rotateGolden))
    }
}
