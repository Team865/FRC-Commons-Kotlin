package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromDegrees
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


    fun setInverted(inverted: Boolean) {
        invertMultiplier = inverted.toDoubleSign()
    }

    fun setMirrored(mirrored: Boolean) {
        mirroredMultiplier = mirrored.toDoubleSign()
    }

    fun setFollower(f: TrajectoryFollower) {
        follower = f
    }

    @ExperimentalTrajectoryFeature
    fun setMixParam(on: Boolean) {
        enableMixParam = on
    }

    fun wheelbaseRadius(metres: Double) {
        wheelbaseRadius = metres
    }

    fun trajectoryVelocity(metresPerSecond: Double) {
        trajectoryVelocity = metresPerSecond
    }

    fun trajectoryAcceleration(metresPerSecondSquared: Double) {
        trajectoryAcceleration = metresPerSecondSquared
    }

    fun jerkLimit(metresPerSecondCubed: Double) {
        maxJerk = metresPerSecondCubed
    }

    fun noJerkLimit() {
        maxJerk = Double.POSITIVE_INFINITY
    }

    fun centripetalAcceleration(hertz: Double) {
        maxCentripetalAcceleration = hertz
    }

    fun bendFactor(factor: Double) {
        bendFactor = factor
    }

    fun setOptimization(on: Boolean) {
        optimizeDkSquared = on
    }

    fun startAt(pose: Pose2D) {
        check(waypoints.isEmpty())
        waypoints.add(pose)
    }

    fun forward(metres: Double) {
        check(waypoints.isNotEmpty() && metres > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation + rotation.translation * metres, rotation) }
        waypoints.add(pose)
    }

    fun reverse(metres: Double) {
        check(waypoints.isNotEmpty() && metres > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation + rotation.translation * (-metres), rotation) }
        waypoints.add(pose)
    }

    @ExperimentalTrajectoryFeature
    fun turnRight(degrees: Double) {
        check(waypoints.isNotEmpty() && degrees > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation, rotation + Rotation2D.fromDegrees(-degrees)) }
        waypoints.add(pose)
    }

    @ExperimentalTrajectoryFeature
    fun turnLeft(degrees: Double) {
        check(waypoints.isNotEmpty() && degrees > 0)
        val pose = waypoints.last()
                .run { Pose2D(translation, rotation + Rotation2D.fromDegrees(degrees)) }
        waypoints.add(pose)
    }

    fun moveTo(pose: Pose2D) {
        check(waypoints.isNotEmpty() && !pose.epsilonEquals(waypoints.last()))
        waypoints.add(pose)
    }

    fun moveToAll(vararg poses: Pose2D) {
        for (pose in poses) {
            moveTo(pose)
        }
    }
}