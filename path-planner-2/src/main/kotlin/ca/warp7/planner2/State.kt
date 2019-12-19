package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.optimized
import ca.warp7.frc.path.quinticSplineFromPose
import ca.warp7.frc.path.sumDCurvature2
import ca.warp7.frc.trajectory.*
import javafx.scene.image.Image


object Constants {
    const val kFieldSize = 8.2296 // metres; equals 27 ft

    const val kTranslationStep = 0.1 // m
    const val kRotationStep = 1.0 // deg
}

/**
 * Defines functions to scale translations
 * to pixel sizes on the screen
 *
 * In JavaFX, x is to the right, y is down, origin at top-left
 *
 * In Path Planner coordinates, x is up, y is left,
 * origin is at the bottom-centre of the image
 *
 * The border is just extra. No calculations involved
 */
class PixelReference {

    private var pixelPerMetreWidth = 0.0
    private var pixelPerMetreHeight = 0.0

    private var originX = 0.0
    private var originY = 0.0

    fun set(
            width: Double,
            height: Double,
            offsetX: Double,
            offsetY: Double
    ) {
        pixelPerMetreWidth = width / Constants.kFieldSize
        pixelPerMetreHeight = height / Constants.kFieldSize

        originX = offsetX + width / 2.0
        originY = offsetY + height
    }

    fun scale(point: Translation2D) = Translation2D(
            -pixelPerMetreWidth * point.y,
            -pixelPerMetreHeight * point.x
    )

    fun inverseScale(point: Translation2D) = Translation2D(
            -point.y / pixelPerMetreWidth,
            -point.x / pixelPerMetreHeight
    )

    fun transform(point: Translation2D) = Translation2D(
            originX - pixelPerMetreWidth * point.y,
            originY - pixelPerMetreHeight * point.x
    )

    fun inverseTransform(point: Translation2D) = Translation2D(
            (originX - point.y) / pixelPerMetreWidth,
            (originY - point.x) / pixelPerMetreHeight
    )
}

/**
 * The part of the State that the user sets in a
 * dialog box
 */
class Configuration {
    var wheelbaseRadius = 0.0
    var maxVelocity = 0.0
    var maxAcceleration = 0.0
    var maxCentripetalAcceleration = 0.0
    var maxJerk = Double.POSITIVE_INFINITY
    var robotWidth = 0.0
    var robotLength = 0.0
    var bg: Image? = null
    var tangentCircle = false
    var angularGraph = false
}

abstract class Segment {
    var waypoints: List<Pose2D> = emptyList()
    var trajectory: List<TrajectoryState> = emptyList()

    var curvatureSum = 0.0
    var arcLength = 0.0
    var trajectoryTime = 0.0
    var maxK = 0.0
    var maxAngular = 0.0
    var maxAngularAcc = 0.0
}

/**
 * The state of the path planner
 */
class State {

    val config = Configuration()
    val reference = PixelReference()
    val segments = ArrayList<Segment>()

    var maxVRatio = 1.0
    var maxARatio = 1.0
    var maxAcRatio = 1.0
    var jerkLimiting = false
    var optimizing = false
    var bendFactor = 1.2

    var selectedSegment = -1
    var selectedPoint = -1
    var selectionChanged = false

    var draggingPoint = false
    var draggingAngle = false
    var draggedControlPoint: Pose2D? = null

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
                config.maxVelocity,
                config.maxAcceleration,
                config.maxCentripetalAcceleration,
                config.maxJerk
        )
        return trajectory
    }

    private fun generateQuickTurn(a: Pose2D, b: Pose2D): List<TrajectoryState> {
        return parameterizeQuickTurn(
                parameterizeRotation(a, b),
                config.maxVelocity / config.wheelbaseRadius,
                config.maxAcceleration / config.wheelbaseRadius
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

    private fun generateSegment(segment: Segment) {

        segment.curvatureSum = 0.0
        segment.arcLength = 0.0

        val trajectory = mutableListOf<TrajectoryState>()
        val path = mutableListOf<QuinticSegment2D>()

        for (i in 0 until segment.waypoints.size - 1) {
            val a = segment.waypoints[i]
            val b = segment.waypoints[i + 1]
            if (!a.translation.epsilonEquals(b.translation)) {
                path.add(quinticSplineFromPose(segment.waypoints[i], segment.waypoints[i + 1], bendFactor))
            } else {
                if (path.isNotEmpty()) {
                    addSplineTrajectory(segment, path, trajectory)
                }
                addToTrajectory(trajectory, generateQuickTurn(a, b))
            }
        }
        if (path.isNotEmpty()) {
            addSplineTrajectory(segment, path, trajectory)
        }

        segment.trajectory = trajectory
        segment.trajectoryTime = trajectory.last().t
    }

    fun generateAll() {

        assertPositive(config.maxVelocity, "maxVelocity")
        assertPositive(config.maxAcceleration, "maxAcceleration")
        assertPositive(config.maxCentripetalAcceleration, "maxCentripetalAcceleration")
        assertPositive(config.wheelbaseRadius, "wheelbaseRadius")

        for (segment in segments) {
            generateSegment(segment)
        }

        totalSumOfCurvature = segments.sumByDouble { it.curvatureSum }
        totalDist = segments.sumByDouble { it.arcLength }
        totalTime = segments.sumByDouble { it.trajectoryTime }
    }
}
