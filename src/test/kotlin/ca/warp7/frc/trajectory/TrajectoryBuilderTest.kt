package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TrajectoryBuilderTest {
    @Test
    fun testBasicSteps() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
        builder.moveTo(Pose2D(1.0, 0.0, 0.0))
        assertEquals(2, builder.waypoints.size)
    }

    @Test
    fun testRelativeSteps() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
        builder.translate(1.0, 0.0)
        assertEquals(Pose2D(1.0, 0.0, 0.0), builder.waypoints.last())
        builder.rotate(90.0)
        assertEquals(Pose2D(1.0, 0.0, Rotation2D.fromDegrees(90.0)), builder.waypoints.last())
    }

    @Test
    fun testNoStartingPoint() {
        val builder = TrajectoryBuilder()
        assertThrows<IllegalStateException> { builder.moveTo(Pose2D()) }
    }

    @Test
    fun testStartingPointWithWaypoints() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
        assertThrows<IllegalStateException> { builder.startAt(Pose2D()) }
    }

    @Test
    fun testUnsetParameters() {
        val builder = TrajectoryBuilder()
        assertThrows<IllegalStateException> { builder.generateTrajectory() }
    }

    @Test
    fun testUnfinishedPoints() {
        val builder = TrajectoryBuilder()
        builder
                .startAt(Pose2D())
                .setMaxVelocity(3.0)
                .setMaxAcceleration(3.0)
                .setWheelbaseRadius(0.6)
        assertEquals(0, builder.generateTrajectory().size)
    }

    @Test
    fun testBasicTrajectory() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
                .moveTo(Pose2D(1.0, 0.0, 0.0))
                .setMaxVelocity(3.0)
                .setMaxAcceleration(3.0)
                .setWheelbaseRadius(0.6)
        val t = builder.generateTrajectory()
        assertEquals(13, t.size)
    }

    @Test
    fun testBasicQuickTurn() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
                .rotate(90.0)
                .setMaxVelocity(3.0)
                .setMaxAcceleration(3.0)
                .setWheelbaseRadius(0.6)
        assertDoesNotThrow {
            builder.generateTrajectory()
        }
    }

    @Test
    fun testQuickTurnWithSpline() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
                .translate(1.0, 0.0)
                .rotate(90.0)
                .setMaxVelocity(3.0)
                .setMaxAcceleration(3.0)
                .setWheelbaseRadius(0.6)
        assertDoesNotThrow {
            builder.generateTrajectory()
        }
    }

    @Test
    fun testQuickTurnWithTwoSpline() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
                .translate(1.0, 0.0)
                .rotate(90.0)
                .translate(1.0, 0.0)
                .setMaxVelocity(3.0)
                .setMaxAcceleration(3.0)
                .setWheelbaseRadius(0.6)
        assertDoesNotThrow {
            builder.generateTrajectory()
        }
    }

    @Test
    fun testTwoQuickTurnWithSpline() {
        val builder = TrajectoryBuilder()
        builder.startAt(Pose2D())
                .rotate(45.0)
                .translate(1.0, 0.0)
                .rotate(-90.0)
                .translate(0.5, 0.0)
                .setMaxVelocity(3.0)
                .setMaxAcceleration(3.0)
                .setWheelbaseRadius(0.6)
        assertDoesNotThrow {
            builder.generateTrajectory()
        }
    }
}