@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.planner2

import ca.warp7.frc.f1
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.optimized
import ca.warp7.frc.path.quinticSplineFromPose
import ca.warp7.frc.path.sumDCurvature2
import ca.warp7.frc.trajectory.*
import kotlin.math.abs

/**
 * The state of the path planner
 */
class State {

    val config = Configuration()
    val reference = PixelReference()
    val segments = ArrayList<Segment>()
    val controlPoints = ArrayList<ControlPoint>()

    var maxVRatio = 1.0
    var maxARatio = 1.0
    var maxAcRatio = 1.0
    var jerkLimiting = false
    var optimizing = false

    var maxAngular = 0.0
    var maxAngularAcc = 0.0

    var totalTime = 0.0
    var totalSumOfCurvature = 0.0
    var totalDist = 0.0

    private fun generateSplineTrajectory(path: List<QuinticSegment2D>): List<TrajectoryState> {
        val optimizedPath = if (optimizing) path.optimized() else path
        val trajectory = optimizedPath.parameterized()
        parameterizeTrajectory(
                trajectory,
                config.wheelbaseRadius,
                config.maxVelocity * maxVRatio,
                config.maxAcceleration * maxARatio,
                config.maxCentripetalAcceleration * maxAcRatio,
                if (jerkLimiting) config.maxJerk else Double.POSITIVE_INFINITY
        )
        return trajectory
    }

    private fun generateQuickTurn(a: Pose2D, b: Pose2D): List<TrajectoryState> {
        return parameterizeQuickTurn(
                parameterizeRotation(a, b),
                config.maxVelocity * maxVRatio / config.wheelbaseRadius,
                config.maxAcceleration * maxARatio / config.wheelbaseRadius
        )
    }

    private fun assertPositive(value: Double, name: String) {
        if (value <= 0) {
            throw IllegalStateException("$name can only be a positive value; is $value")
        }
    }

    private fun addToTrajectory(
            trajectory: MutableList<TrajectoryState>,
            newTrajectory: List<TrajectoryState>
    ) {
        if (trajectory.isEmpty()) {
            trajectory.addAll(newTrajectory)
        } else {
            val lastTrajectoryTime = trajectory.last().t
            // Add time from the last trajectory to this one
            newTrajectory.forEach { it.t += lastTrajectoryTime }
            // Remove the first state from the added list because it should be
            // the same as trajectory's last state
            trajectory.addAll(newTrajectory.subList(1, newTrajectory.size))
        }
    }

    private fun addSplineTrajectory(
            segment: Segment,
            path: MutableList<QuinticSegment2D>,
            trajectory: MutableList<TrajectoryState>
    ) {
        val newTrajectory = generateSplineTrajectory(path)
        addToTrajectory(trajectory, newTrajectory)
        segment.curvatureSum += path.sumDCurvature2()
        segment.arcLength += newTrajectory
                .zipWithNext { p, q -> p.pose.translation.distanceTo(q.pose.translation) }.sum()
        path.clear()
    }

    private fun generateSegment(seg: Segment) {

        seg.curvatureSum = 0.0
        seg.arcLength = 0.0

        val trajectory = mutableListOf<TrajectoryState>()
        val path = mutableListOf<QuinticSegment2D>()

        for (i in 0 until seg.waypoints.size - 1) {
            val a = seg.waypoints[i]
            val b = seg.waypoints[i + 1]
            if (!a.translation.epsilonEquals(b.translation)) {
                path.add(quinticSplineFromPose(seg.waypoints[i],
                        seg.waypoints[i + 1], seg.bendFactor))
            } else {
                if (path.isNotEmpty()) {
                    addSplineTrajectory(seg, path, trajectory)
                }
                addToTrajectory(trajectory, generateQuickTurn(a, b))
            }
        }
        if (path.isNotEmpty()) {
            addSplineTrajectory(seg, path, trajectory)
        }

        seg.trajectory = trajectory
        seg.trajectoryTime = trajectory.last().t
        seg.maxAngular = trajectory.map { it.w }.max()!!
        seg.maxAngularAcc = trajectory.map { it.dw }.max()!!
        seg.maxCurvature = trajectory.map { abs(it.curvature) }.max()!!
    }

    fun generateAll() {

        assertPositive(config.maxVelocity, "maxVelocity")
        assertPositive(config.maxAcceleration, "maxAcceleration")
        assertPositive(config.maxCentripetalAcceleration, "maxCentripetalAcceleration")
        assertPositive(config.wheelbaseRadius, "wheelbaseRadius")
        check(segments.isNotEmpty()) {
            "Cannot generate with no segments"
        }

        for (segment in segments) {
            generateSegment(segment)
        }

        totalSumOfCurvature = segments.sumByDouble { it.curvatureSum }
        totalDist = segments.sumByDouble { it.arcLength }
        totalTime = segments.sumByDouble { it.trajectoryTime }
        maxAngular = segments.map { it.maxAngular }.max()!!
        maxAngularAcc = segments.map { it.maxAngularAcc }.max()!!
    }

    fun maxVelString(): String {
        return "${config.maxVelocity.f1}m/s×${maxVRatio.f1}"
    }

    fun maxAccString(): String {
        return "${config.maxAcceleration.f1}m/s×${maxARatio.f1}"
    }

    fun maxAcString(): String {
        return "${config.maxCentripetalAcceleration.f1}m/s×${maxAcRatio.f1}"
    }
}