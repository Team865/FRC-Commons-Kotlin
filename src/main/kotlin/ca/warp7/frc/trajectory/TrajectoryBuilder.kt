package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.optimized
import ca.warp7.frc.path.quinticSplineFromPose

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryBuilder {

    private var wheelbaseRadius = 0.0
    private var maxVelocity = 0.0
    private var maxAcceleration = 0.0
    private var maxCentripetalAcceleration = 0.0
    private var maxJerk = Double.POSITIVE_INFINITY
    private var bendFactor = 1.2
    private var optimizeDkSquared = false

    internal val waypoints: MutableList<Pose2D> = mutableListOf()
    internal var inverted = false

    fun setInverted(inverted: Boolean) = apply {
        this.inverted = inverted
    }

    fun setWheelbaseRadius(metres: Double) = apply {
        wheelbaseRadius = metres
    }

    fun setMaxVelocity(metresPerSecond: Double) = apply {
        maxVelocity = metresPerSecond
    }

    fun setMaxAcceleration(metresPerSecondSquared: Double) = apply {
        maxAcceleration = metresPerSecondSquared
    }

    fun setJerkLimit(metresPerSecondCubed: Double) = apply {
        maxJerk = metresPerSecondCubed
    }

    fun noJerkLimit() = apply {
        maxJerk = Double.POSITIVE_INFINITY
    }

    fun setMaxCentripetalAcceleration(hertz: Double) = apply {
        maxCentripetalAcceleration = hertz
    }

    fun setBendFactor(factor: Double) = apply {
        bendFactor = factor
    }

    fun setIterativePathOptimization(on: Boolean) = apply {
        optimizeDkSquared = on
    }

    fun startAt(pose: Pose2D) = apply {
        check(waypoints.isEmpty())
        waypoints.add(pose)
    }

    fun forward(metres: Double) = apply {
        check(waypoints.isNotEmpty() && metres > 0)
        waypoints.add(waypoints.last() + Pose2D(metres, 0.0, 0.0))
    }

    fun reverse(metres: Double) = apply {
        check(waypoints.isNotEmpty() && metres > 0)
        waypoints.add(waypoints.last() + Pose2D(-metres, 0.0, 0.0))
    }

    fun turnRight(degrees: Double) = apply {
        check(waypoints.isNotEmpty() && degrees > 0)
        waypoints.add(waypoints.last() + Pose2D(0.0, 0.0, Rotation2D.fromDegrees(-degrees)))
    }

    fun turnLeft(degrees: Double) = apply {
        check(waypoints.isNotEmpty() && degrees > 0)
        waypoints.add(waypoints.last() + Pose2D(0.0, 0.0, Rotation2D.fromDegrees(degrees)))
    }

    fun moveTo(pose: Pose2D) = apply {
        check(waypoints.isNotEmpty() && !pose.epsilonEquals(waypoints.last()))
        waypoints.add(pose)
    }

    private fun generateTrajectory(path: List<QuinticSegment2D>): List<TrajectoryState> {
        val optimizedPath = if (optimizeDkSquared) path.optimized() else path
        val trajectory = optimizedPath.parameterized()
        parameterizeTrajectory(trajectory, wheelbaseRadius,
                maxVelocity, maxAcceleration, maxCentripetalAcceleration, maxJerk)
        return trajectory
    }

    private fun generateQuickTurn(a: Pose2D, b: Pose2D): List<TrajectoryState> {
        return parameterizeQuickTurn(parameterizeRotation(a.rotation, b.rotation),
                maxVelocity / wheelbaseRadius,
                maxAcceleration / wheelbaseRadius)
    }

    internal fun generatePathAndTrajectory(): List<TrajectoryState> {
        val trajectory = mutableListOf<TrajectoryState>()
        val path = mutableListOf<QuinticSegment2D>()

        for (i in 0 until waypoints.size - 1) {
            val a = waypoints[i]
            val b = waypoints[i + 1]
            if (!a.translation.epsilonEquals(b.translation)) {
                path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1], bendFactor))
            } else {
                if (path.isNotEmpty()) {
                    trajectory.addAll(generateTrajectory(path))
                    path.clear()
                }
                trajectory.addAll(generateQuickTurn(a, b))
            }
        }
        if (path.isNotEmpty()) {
            trajectory.addAll(generateTrajectory(path))
            path.clear()
        }
        return trajectory
    }
}