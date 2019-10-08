package test.ca.warp7.frc.geometry

import ca.warp7.frc.geometry.Translation2D
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Translation2DTest {

    @Test
    fun toStringWorksProperly(){
        val transTest = Translation2D(5.5, 4.0).toString()
        val transGolden = "↘(5.500, 4.000)"
        assertEquals(transTest, transGolden)
    }

    @Test
    fun scaledWorksProperly(){
        val transTest = Translation2D(4.0, 6.0).scaled(3.0)
        val transGolden = Translation2D(12.0, 18.0)
	assert(transTest.epsilonEquals(transGolden))
    }

    @Test
    fun timesWorksProperly(){
        val transTest = Translation2D(5.5, 2.0).times(4.0)
        val transGolden = Translation2D(22.0, 8.0)
        assert(transTest.epsilonEquals(transGolden))
    }

    @Test
    fun divWorksProperly(){
        val transTest = Translation2D(12.8, 16.4).div(4.0)
        val transGolden = Translation2D(3.2, 4.1)
        assert(transTest.epsilonEquals(transGolden))
    }
    
    @Test
    fun magWorksProperly() {
        val trans = Translation2D(3.0, 4.0)
        assertEquals(trans.mag, 5.0)
    }
}
