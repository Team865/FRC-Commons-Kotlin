@file:JvmName("SplineParameterizer")

package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.quinticSplinesOf

fun parameterizedSplinesOf(waypoints: List<Pose2D>): List<TrajectoryState> =
        quinticSplinesOf(waypoints).parameterized()

fun List<QuinticSegment2D>.parameterized(): List<TrajectoryState> {
    val points = mutableListOf<TrajectoryState>()
    val p0 = first().getState(0.0)
    points.add(TrajectoryState(p0.pose(), p0.curvature()))
    forEach { points.addAll(it.parameterized()) }
    return points
}

fun QuinticSegment2D.parameterized(): List<TrajectoryState> {
    val points = mutableListOf<TrajectoryState>()
    parameterize(points, 0.0, 1.0)
    return points
}


fun QuinticSegment2D.parameterize(
        points: MutableList<TrajectoryState>,
        t0: Double,
        t1: Double
) {
    val state = getState(t1)
    val statePose = state.pose()
    val twist = (statePose - getPose(t0)).log()
    if (twist.dx > 0.127 || twist.dy > 0.00127 || twist.dTheta > 0.0872) {
        parameterize(points, t0, (t0 + t1) / 2.0)
        parameterize(points, (t0 + t1) / 2.0, t1)
    } else {
        points.add(TrajectoryState(statePose, state.curvature()))
    }
}

/**
 * Distance-parameterize between two Rotations
 *
 * Uses degrees because this makes printing much easier to read
 */
fun parameterizeRotation(a: Pose2D, b: Pose2D): List<TrajectoryState> {
    val startingAngle = a.rotation.degrees()
    val theta = (b - a).rotation.degrees()
    require(theta != 0.0) {
        "QuickTurn Generator - Two points are the same"
    }
    val quickTurnAngles = mutableListOf<Rotation2D>()
    var x = 0.0
    return if (theta > 0) {
        while (x < theta) {
            quickTurnAngles.add(Rotation2D.fromDegrees(startingAngle + x))
            x += 5
        }
        quickTurnAngles.add(b.rotation)
        quickTurnAngles.map { TrajectoryState(Pose2D(a.translation, it), Double.POSITIVE_INFINITY) }
    } else {
        while (x > theta) {
            quickTurnAngles.add(Rotation2D.fromDegrees(startingAngle + x))
            x -= 5
        }
        quickTurnAngles.add(b.rotation)
        quickTurnAngles.map { TrajectoryState(Pose2D(a.translation, it), Double.NEGATIVE_INFINITY) }
    }
}