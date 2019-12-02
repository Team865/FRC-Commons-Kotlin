package ca.warp7.frc.geometry

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//This is testing the Translation2D class using unittesting

class Translation2DTest {

    @Test
    fun interpolateWorksProperly() {
        val transTestA0 = Translation2D(100.0, 100.0)
        val transTestA1 = Translation2D(0.0, 0.0)
        val transTestA2 = transTestA0.interpolate(transTestA1, 0.5)
        val transGoldenA = Translation2D(50.0, 50.0)

        assertTrue(transTestA2.epsilonEquals(transGoldenA))
    }

    @Test
    fun interpolateWorksProperly2() {

        val transTestB0 = Translation2D(100.0, 100.0)
        val transTestB1 = Translation2D(200.0, 200.0)
        val transTestB2 = transTestB0.interpolate(transTestB1, 0.75)
        val transGoldenB = Translation2D(175.0, 175.0)

        assertTrue(transTestB2.epsilonEquals(transGoldenB))
    }
//
//    @Test
//    fun unaryMinusWorksProperly() {
//        val transTest0 = Translation2D(-10.1, 15.9).unaryMinus()
//        val transTest1 = -Translation2D(-10.1, 15.9)
//        val transGolden = Translation2D(10.1, -15.9)
//
//        assertTrue(transTest0.epsilonEquals(transGolden))
//        assertTrue(transTest1.epsilonEquals(transGolden))
//    }

    @Test
    fun inverseWorksProperly() {
        val transTest = Translation2D(1425.0, -23.0).inverse
        val transGolden = Translation2D(-1425.0, 23.0)
        assertTrue(transTest.epsilonEquals(transGolden))
    }

//    @Test
//    fun unaryPlusWorksProperly() {
//        val transTest0 = Translation2D(25.0, 3.34501).unaryPlus()
//        val transTest1 = +Translation2D(25.0, 3.34501)
//        val transGolden = Translation2D(25.0, 3.34501)
//
//        assert(transTest0.epsilonEquals(transGolden))
//        assert(transTest1.epsilonEquals(transGolden))
//    }
//
//    @Test
//    fun copyWorksProperly() {
//        val transTest = Translation2D(20.01, 13.49).copy
//        val transGolden = Translation2D(20.01, 13.49)
//        assert(transTest.epsilonEquals(transGolden))
//    }

    @Test
    fun epsilonEqualsWorksProperly() {
        val transEqualGolden = Translation2D(10.0 / 5.0, 15.0 / 3.0)
        val transEqual = Translation2D(2.0, 5.0).epsilonEquals(transEqualGolden)

        val transNotEqualGolden = Translation2D(-10.000000001, 24.000000001)
        val transNotEqual = Translation2D(-10.0, 24.0).epsilonEquals(transNotEqualGolden)

        assertTrue(transEqual)
        assertEquals(transNotEqual, false)
    }

//    @Test
//    fun transformWorksProperly() {
//        val transForm = Translation2D(-5.0, 5.0)
//        val transTest = Translation2D(10.0, 10.0).transform(transForm)
//        val transGolden = Translation2D(5.0, 15.0)
//        assert(transTest.epsilonEquals(transGolden))
//    }

    @Test
    fun plusWorksProperly() {
        val transAdd = Translation2D(9.0, 4.5)
        val transTest = Translation2D(50.0, 5.5).plus(transAdd)
        val transGolden = Translation2D(59.0, 10.0)
        assertTrue(transTest.epsilonEquals(transGolden))
    }

    @Test
    fun minusWorksProperly() {
        val transSub = Translation2D(4.0, -2.0)
        val transTest = Translation2D(8.0, 8.0).minus(transSub)
        val transGolden = Translation2D(4.0, 10.0)
        assertTrue(transTest.epsilonEquals(transGolden))
    }

    @Test
    fun toStringWorksProperly() {
        val transTest = Translation2D(5.5, 4.0).toString()
        val transGolden = "↘(5.500, 4.000)"
        assertEquals(transTest, transGolden)
    }

    @Test
    fun scaledWorksProperly() {
        val transTest = Translation2D(4.0, 6.0).scaled(3.0)
        val transGolden = Translation2D(12.0, 18.0)
        assertTrue(transTest.epsilonEquals(transGolden))
    }

    @Test
    fun timesWorksProperly() {
        val transTest0 = Translation2D(5.5, 2.0).times(4.0)
        val transTest1 = Translation2D(5.5, 2.0) * 4.0
        val transGolden = Translation2D(22.0, 8.0)

        assertTrue(transTest0.epsilonEquals(transGolden))
        assertTrue(transTest1.epsilonEquals(transGolden))
    }

    @Test
    fun divWorksProperly() {
        val transTest0 = Translation2D(12.8, 16.4).div(4.0)
        val transTest1 = Translation2D(12.8, 16.4) / 4.0
        val transGolden = Translation2D(3.2, 4.1)

        assertTrue(transTest0.epsilonEquals(transGolden))
        assertTrue(transTest1.epsilonEquals(transGolden))
    }

    @Test
    fun magWorksProperly() {
        val trans = Translation2D(3.0, 4.0)
        assertEquals(trans.mag, 5.0)
    }
}
