package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.path.optimized
import ca.warp7.frc.path.quinticSplineFromPose

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TrajectoryBuilder {

    private var wheelbaseRadius = 0.0
    private var maxVelocity = 0.0
    private var maxAcceleration = 0.0
    private var maxCentripetalAcceleration = Double.POSITIVE_INFINITY
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

    fun moveTo(pose: Pose2D) = apply {
        check(waypoints.isNotEmpty())
        waypoints.add(pose)
    }

    fun translate(forward: Double, lateral: Double) = apply {
        check(waypoints.isNotEmpty())
        waypoints.add(waypoints.last() + Pose2D(forward, lateral, 0.0))
    }

    fun rotate(degrees: Double) = apply {
        check(waypoints.isNotEmpty())
        waypoints.add(waypoints.last() + Pose2D(0.0, 0.0, Rotation2D.fromDegrees(degrees)))
    }

    fun moveRelatively(forward: Double, lateral: Double, degrees: Double) = apply {
        check(waypoints.isNotEmpty())
        waypoints.add(waypoints.last() + Pose2D(forward, lateral, Rotation2D.fromDegrees(degrees)))
    }

    private fun generateSplineTrajectory(path: List<QuinticSegment2D>): List<TrajectoryState> {
        val optimizedPath = if (optimizeDkSquared) path.optimized() else path
        val trajectory = optimizedPath.parameterized()
        parameterizeTrajectory(trajectory, wheelbaseRadius,
                maxVelocity, maxAcceleration, maxCentripetalAcceleration, maxJerk)
        return trajectory
    }

    private fun generateQuickTurn(a: Pose2D, b: Pose2D): List<TrajectoryState> {
        return parameterizeQuickTurn(parameterizeRotation(a, b),
                maxVelocity / wheelbaseRadius,
                maxAcceleration / wheelbaseRadius)
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

    internal fun generateTrajectory(): List<TrajectoryState> {

        assertPositive(maxVelocity, "maxVelocity")
        assertPositive(maxAcceleration, "maxAcceleration")
        assertPositive(maxCentripetalAcceleration, "maxCentripetalAcceleration")
        assertPositive(wheelbaseRadius, "wheelbaseRadius")

        val trajectory = mutableListOf<TrajectoryState>()
        val path = mutableListOf<QuinticSegment2D>()

        for (i in 0 until waypoints.size - 1) {
            val a = waypoints[i]
            val b = waypoints[i + 1]
            if (!a.translation.epsilonEquals(b.translation)) {
                path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1], bendFactor))
            } else {
                if (path.isNotEmpty()) {
                    addToTrajectory(trajectory, generateSplineTrajectory(path))
                    path.clear()
                }
                addToTrajectory(trajectory, generateQuickTurn(a, b))
            }
        }
        if (path.isNotEmpty()) {
            addToTrajectory(trajectory, generateSplineTrajectory(path))
            path.clear()
        }
        return trajectory
    }
}