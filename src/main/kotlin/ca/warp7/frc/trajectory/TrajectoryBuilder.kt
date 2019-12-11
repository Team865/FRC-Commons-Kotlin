package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.*
import ca.warp7.frc.path.*

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

    internal var follower: TrajectoryFollower? = null

    internal var invertMultiplier = 1.0
    internal var mirroredMultiplier = 1.0


    fun setInverted(inverted: Boolean) = apply {
        invertMultiplier = if (inverted) -1.0 else 1.0
    }

    fun setMirrored(mirrored: Boolean) = apply {
        mirroredMultiplier = if (mirrored) -1.0 else 1.0
    }

    fun setFollower(f: TrajectoryFollower) = apply {
        follower = f
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
        waypoints.add(waypoints.last() + Pose2D(0.0, 0.0, Rotation2D.fromRadians(-degrees)))
    }

    fun turnLeft(degrees: Double) = apply {
        check(waypoints.isNotEmpty() && degrees > 0)
        waypoints.add(waypoints.last() + Pose2D(0.0, 0.0, Rotation2D.fromRadians(degrees)))
    }

    fun moveTo(pose: Pose2D) = apply {
        check(waypoints.isNotEmpty() && !pose.epsilonEquals(waypoints.last()))
        waypoints.add(pose)
    }

    private fun generateTrajectory(path: List<QuinticSegment2D>): List<TrajectoryState> {
        val optimizedPath = if (optimizeDkSquared) path.optimized() else path
        val parameterizedPath = optimizedPath.parameterized()
        return generateTrajectory(parameterizedPath, wheelbaseRadius,
                maxVelocity, maxAcceleration, maxCentripetalAcceleration, maxJerk)
    }

    private fun generateQuickTurn(a: Pose2D, b: Pose2D): List<TrajectoryState> {
        return generateQuickTurn(parameterizeQuickTurn(a.rotation, b.rotation),
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