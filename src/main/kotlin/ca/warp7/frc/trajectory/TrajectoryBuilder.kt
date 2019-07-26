package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D

interface TrajectoryBuilder {
    fun wheelbaseRadius(metres: Double)

    fun trajectoryVelocity(metresPerSecond: Double)

    fun trajectoryAcceleration(metresPerSecondSquared: Double)

    fun jerkLimit(metresPerSecondCubed: Double)

    fun noJerkLimit()

    fun centripetalAcceleration(hertz: Double)

    fun adjustMaxVelocity(scale: Double)

    fun adjustMaxAcceleration(scale: Double)

    fun adjustMaxCentripetalAcceleration(scale: Double)

    fun adjustMaxJerk(scale: Double)

    fun startAt(pose: Pose2D)

    fun startFromRobotState(getState: () -> Pose2D)

    fun forward(metres: Double)

    fun reverse(metres: Double)

    fun turnRight(degrees: Double)

    fun turnLeft(degrees: Double)

    fun moveTo(pose: Pose2D)

    fun conformFactor(factor: Double)
}

fun test0(b: TrajectoryBuilder.() -> Unit) {

}

fun test() {
    test0 {
        trajectoryVelocity(12.0)
        trajectoryAcceleration(1.0)
        turnLeft(90.0)
        adjustMaxVelocity(1.0)
        adjustMaxAcceleration(4.0)
        forward(4.0)
    }
}