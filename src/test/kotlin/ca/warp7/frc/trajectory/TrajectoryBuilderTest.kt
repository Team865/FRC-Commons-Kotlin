package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TrajectoryBuilderTest {
    @Test
    fun testBasicSteps() {
        val builder = TrajectoryBuilder()
        assertEquals(1, builder.invertMultiplier)
        assertEquals(1, builder.mirroredMultiplier)
        builder.setInverted(true)
        builder.setMirrored(true)
        assertEquals(-1, builder.invertMultiplier)
        assertEquals(-1, builder.mirroredMultiplier)
        builder.startAt(Pose2D.identity)
        builder.moveTo(Pose2D(1.0, 0.0, 0.0))
        assertEquals(2, builder.waypoints.size)
    }

    @Test
    fun testRelativeSteps() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D.identity)
        builder.forward(1.0)
        assertEquals(Pose2D(1.0, 0.0, 0.0), builder.waypoints.last())
        builder.turnLeft(90.0)
        assertEquals(Pose2D(1.0, 0.0, Rotation2D.fromDegrees(90.0)), builder.waypoints.last())
    }

    @Test
    fun testNoStartingPoint() {
        val builder = TrajectoryBuilder()
        assertThrows<IllegalStateException> { builder.moveTo(Pose2D.identity) }
    }

    @Test
    fun testStartingPointWithWaypoints() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D.identity)
        assertThrows<IllegalStateException> { builder.startAt(Pose2D.identity) }
    }
}