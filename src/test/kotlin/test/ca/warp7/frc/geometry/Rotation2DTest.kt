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

    fun scaledWorksProperly(){
	val rotateTest =  Rotation2D(2.4, -23.2).scaled(-2.0)
	val rotateGolden = Rotataion2D(-4.8, 46.4)
	assert(rotateTest.epsilonEquals(rotateGolden))
    }

    fun timesWorksProperly(){
	val rotateTest = Rotation2D(12.0, 2.0).times(3.0)
	val rotateGolden = Rotation2D(36.0, 6.0)
	assert(rotateTest.epsilonEquals(rotateGolden))
    }

    fun divWorksProperly(){
	val rotateTest = Rotation2D(3.5, 2.4).div(2.0)
	val rotateGolden = Rotation2D(1.75, 1.2)
	assert(rotateTest.epsilonEquals(rotateGolden))
    }

    @Test
    fun inverseWorksProperly() {
        val rotateTest = Rotation2D(0.9, 0.05).inverse
        val rotateGolden = Rotation2D(0.9, -0.05)
        assert(rotateTest.epsilonEquals(rotateGolden))
    }
}
