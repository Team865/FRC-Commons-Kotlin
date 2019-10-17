package test.ca.warp7.frc.geometry

import ca.warp7.frc.geometry.Rotation2D
import org.junit.jupiter.api.Test

//This is testing the Rotation2D class using unittesting

class Rotation2DTest {

//    @Test
//    fun unaryMinusWorksProperly() {
//        val rotateTest = Rotation2D(0.89, 0.55).unaryMinus()
//        val rotateGolden = Rotation2D(0.89, -0.55)
//        assert(rotateTest.epsilonEquals(rotateGolden))
//    }

    @Test
    fun inverseWorksProperly() {
        val rotateTest = Rotation2D(0.9, 0.05).inverse
        val rotateGolden = Rotation2D(0.9, -0.05)
        assert(rotateTest.epsilonEquals(rotateGolden))
    }
}
