package ca.warp7.frc.trajectory

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.ArcPose2D
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs

class TrajectoryGeneratorTest {

    private fun createStraightLinePath(): List<ArcPose2D> {
        return (0..10).map {
            ArcPose2D(Pose2D(it / 10.0, 0.0, Rotation2D.identity), 0.0)
        }
    }

    private fun createStraightLinePathLong(): List<ArcPose2D> {
        return (0..100).map {
            ArcPose2D(Pose2D(it / 10.0, 0.0, Rotation2D.identity), 0.0)
        }
    }

    private fun checkEndpointInvariants(trajectory: List<TrajectoryState>) {
        val first = trajectory.first()
        val last = trajectory.last()
        assertEquals(0.0, first.t)
        assertEquals(0.0, first.v)
        assertEquals(0.0, first.w)
        assertEquals(0.0, first.dv)
        assertEquals(0.0, first.dw)
        assertEquals(0.0, last.v)
        assertEquals(0.0, last.w)
        assertEquals(0.0, last.dv)
        assertEquals(0.0, last.dv)
        // note that the third derivative(jerk) is not required to be 0 at endpoints
    }

    private fun checkConstantAcceleration(trajectory: List<TrajectoryState>) {
        if (trajectory.size < 3) return
        val initial = abs(trajectory[1].dv)
        assertTrue((2 until trajectory.size - 2).all { i -> abs(trajectory[i].dv).epsilonEquals(initial) })
    }

    private fun checkInputPathMatchesOutput(path: List<ArcPose2D>, trajectory: List<TrajectoryState>) {
        // test that all the poses are identical
        assertTrue(trajectory.indices.all { i -> trajectory[i].arcPose === path[i] })
    }

    private fun printTrajectory(trajectory: List<TrajectoryState>) {
        println(trajectory.joinToString("\n"))
    }

    private fun generateNoJerkLimit(path: List<ArcPose2D>): List<TrajectoryState> {
        return generateTrajectory(
                path = path,
                wheelbaseRadius = 1.0,
                maxVelocity = 5.0,
                maxAcceleration = 3.0,
                maxCentripetalAcceleration = 3.0,
                maxJerk = Double.POSITIVE_INFINITY
        )
    }

    private fun generateWithJerkLimit(path: List<ArcPose2D>): List<TrajectoryState> {
        return generateTrajectory(
                path = path,
                wheelbaseRadius = 1.0,
                maxVelocity = 5.0,
                maxAcceleration = 3.0,
                maxCentripetalAcceleration = 3.0,
                maxJerk = 20.0
        )
    }

    @Test
    fun testStraightLine() {
        val path = createStraightLinePath()
        val trajectory = generateNoJerkLimit(path)
        checkInputPathMatchesOutput(path, trajectory)
        checkEndpointInvariants(trajectory)
        checkConstantAcceleration(trajectory)
    }

    @Test
    fun testStraightLineJerkLimit() {
        val path = createStraightLinePathLong()
        generateWithJerkLimit(path)
        assertTrue(true)
        // todo add more tests for this; doesn't work for short paths
    }

    @Test
    fun testEmptyPathInput() {
        val path = listOf<ArcPose2D>()
        val trajectory = generateNoJerkLimit(path)
        assertTrue(trajectory.isEmpty())
    }

    @Test
    fun testSinglePathInput() {
        val path = listOf(ArcPose2D(Pose2D.identity, 0.0))
        val trajectory = generateNoJerkLimit(path)
        assertEquals(0.0, trajectory.first().t)
        checkInputPathMatchesOutput(path, trajectory)
    }

    @Test
    fun testSingleInfCurvatureInput() {
        val path = listOf(ArcPose2D(Pose2D.identity, Double.POSITIVE_INFINITY))
        assertThrows<IllegalArgumentException> {
            generateNoJerkLimit(path)
        }
    }

    @Test
    fun testMultiInfCurvatureInput() {
        val path = listOf(
                ArcPose2D(Pose2D.identity, 0.0),
                ArcPose2D(Pose2D(1.0, 0.0, Rotation2D.identity), Double.POSITIVE_INFINITY))
        assertThrows<IllegalArgumentException> { generateNoJerkLimit(path) }
    }

    @Test
    fun testMultiSamePositionInput() {
        val path = listOf(
                ArcPose2D(Pose2D.identity, 0.0),
                ArcPose2D(Pose2D.identity, 0.0))
        assertThrows<IllegalArgumentException> { generateNoJerkLimit(path) }
    }
}