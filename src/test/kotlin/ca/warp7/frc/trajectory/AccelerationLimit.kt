package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Translation2D


/*
Suppose 7 pt straight path.
And We want to properly limit acceleration.
max velocity: 1m/s
max acceleration: 1m/s^2
wheelbase: doesn't matter

eqn:  sqrt(v^2 + 2 * 1m/s^2 * dist)

Initial State

dist -> dt    v       dv        ddv
0.0m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.1m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.2m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.3m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.4m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.5m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.6m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3

After Forward Pass

dist -> dt    v       dv        ddv
0.0m -> 0.0000s  0.0000m/s  0.0m/s^2  0.0m/s^3
0.1m -> 0.4472s  0.4472m/s  0.0m/s^2  0.0m/s^3
0.2m -> 0.1852s  0.6325m/s  0.0m/s^2  0.0m/s^3
0.3m -> 0.0s  0.7745m/s  0.0m/s^2  0.0m/s^3
0.4m -> 0.0s  0.8944m/s  0.0m/s^2  0.0m/s^3
0.5m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3
0.6m -> 0.0s  1.0m/s  0.0m/s^2  0.0m/s^3

 */


fun main() {

    val path2 = (0..7).map { TrajectoryState(Pose2D(it / 10.0, 0.0, 0.0), 0.0) }
    parameterizeTrajectory(path2, 1.0, 1.0, 1.0, 1.0, Double.POSITIVE_INFINITY)
    println(path2.joinToString("\n"))

    println()
    val path = (0..7).map { TrajectoryState(Pose2D(it / 10.0, 0.0, 0.0), 0.0) }
    parameterizeTrajectory(path, 1.0, 1.0, 1.0, 1.0, 5.0)
    println(path.joinToString("\n"))


}