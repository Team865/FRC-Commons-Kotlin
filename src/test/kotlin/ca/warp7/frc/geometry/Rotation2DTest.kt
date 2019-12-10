package ca.warp7.frc.geometry

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

//This is testing the Rotation2D class using unit testing

class Rotation2DTest {


    @Test
    fun scaledWorksProperly() {
        val rotateTest = Rotation2D(2.4, -23.2).scaled(-2.0)
        val rotateGolden = Rotation2D(-4.8, 46.4)
        assertTrue(rotateTest.epsilonEquals(rotateGolden))
    }

    @Test
    fun timesWorksProperly() {
        val rotateTest = Rotation2D(12.0, 2.0).times(3.0)
        val rotateGolden = Rotation2D(36.0, 6.0)
        assertTrue(rotateTest.epsilonEquals(rotateGolden))
    }

    @Test
    fun divWorksProperly() {
        val rotateTest = Rotation2D(3.5, 2.4).div(2.0)
        val rotateGolden = Rotation2D(1.75, 1.2)
        assertTrue(rotateTest.epsilonEquals(rotateGolden))
    }

    @Test
    fun inverseWorksProperly() {
        val rotateTest = Rotation2D(0.9, 0.05).inverse
        val rotateGolden = Rotation2D(0.9, -0.05)
        assertTrue(rotateTest.epsilonEquals(rotateGolden))
    }
}
