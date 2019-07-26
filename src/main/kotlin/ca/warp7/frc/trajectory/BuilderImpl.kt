package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D

class BuilderImpl: TrajectoryBuilder {
    internal var wheelbaseRadius = 0.0

    private var trajectoryVelocity = 0.0
    internal var adjustedVelocity = 0.0

    private var trajectoryAcceleration = 0.0
    internal var adjustedAcceleration = 0.0

    private var maxCentripetalAcceleration = 0.0
    internal var adjustedCentripetalAcceleration = 0.0

    private var maxJerk = 0.0
    internal var adjustedJerk = 0.0

    private var jerkLimit = false

    override fun wheelbaseRadius(metres: Double) {
        wheelbaseRadius = metres
    }

    override fun trajectoryVelocity(metresPerSecond: Double) {
        trajectoryVelocity = metresPerSecond
        adjustMaxVelocity(1.0)
    }

    override fun trajectoryAcceleration(metresPerSecondSquared: Double) {
        trajectoryAcceleration = metresPerSecondSquared
        adjustMaxAcceleration(1.0)
    }

    override fun jerkLimit(metresPerSecondCubed: Double) {
        maxJerk = metresPerSecondCubed
        jerkLimit = true
        adjustMaxJerk(1.0)
    }

    override fun noJerkLimit() {
        jerkLimit = false
    }

    override fun centripetalAcceleration(hertz: Double) {
        maxCentripetalAcceleration = hertz
    }

    override fun adjustMaxVelocity(scale: Double) {
        adjustedVelocity = trajectoryVelocity * scale
    }

    override fun adjustMaxAcceleration(scale: Double) {
        adjustedAcceleration = trajectoryAcceleration * scale
    }

    override fun adjustMaxCentripetalAcceleration(scale: Double) {
        adjustedCentripetalAcceleration = maxCentripetalAcceleration * scale
    }

    override fun adjustMaxJerk(scale: Double) {
        TODO("not implemented")
    }

    override fun startAt(pose: Pose2D) {
        TODO("not implemented")
    }

    override fun startFromRobotState(getState: () -> Pose2D) {
        TODO("not implemented")
    }

    override fun forward(metres: Double) {
        TODO("not implemented")
    }

    override fun reverse(metres: Double) {
        TODO("not implemented")
    }

    override fun turnRight(degrees: Double) {
        TODO("not implemented")
    }

    override fun turnLeft(degrees: Double) {
        TODO("not implemented")
    }

    override fun moveTo(pose: Pose2D) {
        TODO("not implemented")
    }

    override fun conformFactor(factor: Double) {
        TODO("not implemented")
    }
}