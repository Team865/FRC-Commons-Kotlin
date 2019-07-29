package ca.warp7.frc.drive.trajectory

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.geometry.Pose2D

class TrajectoryBuilder(builder: TrajectoryBuilder.() -> Unit) {

    init {
        builder(this)
    }

    private var segmentEnded = false

    private var wheelbaseRadius = 0.0

    private var trajectoryVelocity = 0.0
    private var adjustedVelocity = 0.0

    private var trajectoryAcceleration = 0.0
    private var adjustedAcceleration = 0.0

    private var maxCentripetalAcceleration = 0.0
    private var adjustedCentripetalAcceleration = 0.0

    private var maxJerk = 0.0
    private var adjustedJerk = 0.0

    private var jerkLimit = false

    object NoFollower : TrajectoryFollower {
         override fun updateTrajectory(
                 setpoint: TrajectoryState,
                 error: Pose2D,
                 velocity: ChassisState,
                 acceleration: ChassisState
        ) = Unit
    }

    init {
        turnLeft(40.0)
    }

    private var follower: TrajectoryFollower = NoFollower

    private fun endSegment() {
        segmentEnded = true
    }

     fun setFollower(follower: TrajectoryFollower) {
        this.follower = follower
    }

     fun wheelbaseRadius(metres: Double) {
        wheelbaseRadius = metres
        endSegment()
    }

     fun trajectoryVelocity(metresPerSecond: Double) {
        trajectoryVelocity = metresPerSecond
        adjustMaxVelocity(1.0)
    }

     fun trajectoryAcceleration(metresPerSecondSquared: Double) {
        trajectoryAcceleration = metresPerSecondSquared
        adjustMaxAcceleration(1.0)
    }

     fun jerkLimit(metresPerSecondCubed: Double) {
        maxJerk = metresPerSecondCubed
        jerkLimit = true
        adjustMaxJerk(1.0)
    }

     fun noJerkLimit() {
        jerkLimit = false
        endSegment()
    }

     fun centripetalAcceleration(hertz: Double) {
        maxCentripetalAcceleration = hertz
    }

     fun adjustMaxVelocity(scale: Double) {
        adjustedVelocity = trajectoryVelocity * scale
        endSegment()
    }

     fun adjustMaxAcceleration(scale: Double) {
        adjustedAcceleration = trajectoryAcceleration * scale
        endSegment()
    }

     fun adjustMaxCentripetalAcceleration(scale: Double) {
        adjustedCentripetalAcceleration = maxCentripetalAcceleration * scale
        endSegment()
    }

     fun adjustMaxJerk(scale: Double) {
        adjustedJerk = maxJerk * scale
        endSegment()
    }

     fun startAt(pose: Pose2D) {
        TODO("not implemented")
    }

     fun startFromRobotState(getState: () -> Pose2D) {
        TODO("not implemented")
    }

     fun forward(metres: Double) {
        TODO("not implemented")
    }

     fun reverse(metres: Double) {
        TODO("not implemented")
    }

     fun turnRight(degrees: Double) {
        TODO("not implemented")
    }

     fun turnLeft(degrees: Double) {
        TODO("not implemented")
    }

     fun moveTo(pose: Pose2D) {
        TODO("not implemented")
    }

     fun conformFactor(factor: Double) {
        TODO("not implemented")
    }
}