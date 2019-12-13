package ca.warp7.frc.trajectory

import ca.warp7.frc.degrees
import ca.warp7.frc.geometry.Rotation2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SplineParameterizerTest {
    @Test
    fun testQuickTurnParameterization() {
        val param = parameterizeRotation(Rotation2D.identity, 90.degrees)
        assertEquals(19, param.size)
        assertEquals(Rotation2D.identity, param.first().pose.rotation)
        assertEquals(90.degrees, param.last().pose.rotation)
    }
}