package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.translation
import ca.warp7.frc.toDoubleSign

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryBuilder {

    @Experimental
    annotation class ExperimentalTrajectoryFeature

    internal var wheelbaseRadius = 0.0
    internal var trajectoryVelocity = 0.0
    internal var trajectoryAcceleration = 0.0
    internal var maxCentripetalAcceleration = 0.0
    internal var maxJerk = Double.POSITIVE_INFINITY
    internal var bendFactor = 1.2
    internal var optimizeDkSquared = false
    internal var enableMixParam = false

    internal var follower: TrajectoryFollower? = null

    internal val waypoints: MutableList<Pose2D> = mutableListOf()

    internal var invertMultiplier = 0.0
    internal var mirroredMultiplier = 0.0


    fun setInverted(inverted: Boolean) = apply {
        invertMultiplier = inverted.toDoubleSign()
    }

    fun setMirrored(mirrored: Boolean) = apply {
        mirroredMultiplier = mirrored.toDoubleSign()
    }

    fun setFollower(f: TrajectoryFollower) = apply {
        follower = f
    }

    @ExperimentalTrajectoryFeature
    fun setMixParam(on: Boolean) = apply {
        enableMixParam = on
    }

    fun setWheelbaseRadius(metres: Double) = apply {
        wheelbaseRadius = metres
    }

    fun setTrajectoryVelocity(metresPerSecond: Double) = apply {
        trajectoryVelocity = metresPerSecond
    }

    fun setTrajectoryAcceleration(metresPerSecondSquared: Double) = apply {
        trajectoryAcceleration = metresPerSecondSquared
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
        val pose = waypoints.last()
                .run { Pose2D(translation + rotation.translation * metres, rotation) }
        waypoints.add(pose)
    }

    fun reverse(metres: Double) = apply {
        check(waypoints.isNotEmpty() && metres > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation + rotation.translation * (-metres), rotation) }
        waypoints.add(pose)
    }

    @ExperimentalTrajectoryFeature
    fun turnRight(degrees: Double) = apply {
        check(waypoints.isNotEmpty() && degrees > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation, rotation + Rotation2D.fromDegrees(-degrees)) }
        waypoints.add(pose)
    }

    @ExperimentalTrajectoryFeature
    fun turnLeft(degrees: Double) = apply {
        check(waypoints.isNotEmpty() && degrees > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation, rotation + Rotation2D.fromDegrees(degrees)) }
        waypoints.add(pose)
    }

    fun moveTo(pose: Pose2D) = apply {
        check(waypoints.isNotEmpty() && !pose.epsilonEquals(waypoints.last()))
        waypoints.add(pose)
    }

    fun moveToAll(vararg poses: Pose2D) = apply {
        for (pose in poses) {
            moveTo(pose)
        }
    }
}