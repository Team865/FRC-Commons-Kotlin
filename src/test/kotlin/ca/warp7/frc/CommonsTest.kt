package ca.warp7.frc

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.linearInterpolate
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonsTest {
    @Test
    fun epsilonEquals1() {
        assertTrue(1.0.epsilonEquals(1.0))
    }

    @Test
    fun epsilonEqualsInfinity() {
        assertTrue(Double.POSITIVE_INFINITY.epsilonEquals(Double.POSITIVE_INFINITY))
    }

    @Test
    fun epsilonEqualsSmallDiff() {
        assertTrue(1.0.epsilonEquals(1.000000000000001))
    }

    @Test
    fun epsilonEqualsWithEps() {
        assertTrue(1.0.epsilonEquals(1.0001, 1E-3))
    }

    @Test
    fun linearInterpolateNormal() {
        assertEquals(linearInterpolate(2.0, 8.0, 0.5), 5.0)
    }

    @Test
    fun linearInterpolateMax() {
        assertEquals(linearInterpolate(2.0, 8.0, 1.5), 8.0)
    }

    @Test
    fun linearInterpolateMin() {
        assertEquals(linearInterpolate(2.0, 8.0, -5.0), 2.0)
    }
}