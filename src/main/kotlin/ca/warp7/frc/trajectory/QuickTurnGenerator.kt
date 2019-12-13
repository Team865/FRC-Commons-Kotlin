@file:JvmName("QuickTurnGenerator")

package ca.warp7.frc.trajectory

import ca.warp7.frc.squared
import kotlin.math.sqrt

/**
 * This is a simple version of [parameterizeTrajectory]. Read more docs there.
 * This only works for quick turning
 */
fun parameterizeQuickTurn(
        states: List<TrajectoryState>,
        maxAngularVelocity: Double, // m/s
        maxAngularAcceleration: Double // m/s^2
): List<TrajectoryState> {
    // If path is empty, returns an empty trajectory
    if (states.isEmpty()) {
        return emptyList()
    }

    // Make sure the first state doesn't have infinite curvature
    require(states.first().curvature.isInfinite()) {
        "Normal curvature is not allowed in the quick turn generator"
    }

    val angles = computeAngles(states)
    angularForwardPass(states, angles, maxAngularVelocity, maxAngularAcceleration)
    angularReversePass(states, angles, maxAngularAcceleration)
    accumulativePass(states)
    integrationPass(states)
    return states
}

private fun computeAngles(states: List<TrajectoryState>): List<Double> {
    return states.zipWithNext { current, next ->

        // Check that the path is actually a  path
        require(current.pose.translation.epsilonEquals(next.pose.translation)) {
            "Multiple translations not allowed in the quick turn generator"
        }

        current.pose.rotation.distanceTo(next.pose.rotation)
    }
}

private fun angularForwardPass(
        states: List<TrajectoryState>,
        angles: List<Double>,
        maxAngularVelocity: Double,
        maxAngularAcceleration: Double
) {

    // Assign the initial linear and angular velocity
    states.first().w = 0.0

    for (i in 0 until states.size - 1) {

        val angle = angles[i]
        val current = states[i]
        val next = states[i + 1]

        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableAngularVelocity = sqrt(current.w.squared + 2 * maxAngularAcceleration * angle)

        // Limit velocity based on curvature constraint and forward acceleration
        next.w = minOf(maxAngularVelocity, maxReachableAngularVelocity)

        // Calculate the forward dt
        next.t = (2 * angle) / (current.w + next.w)
    }
}

private fun angularReversePass(
        states: List<TrajectoryState>,
        angles: List<Double>,
        maxAngularAcceleration: Double
) {

    // Assign the final linear and angular velocity
    states.last().w = 0.0

    for (i in states.size - 1 downTo 1) {

        val angle = angles[i - 1]
        val current = states[i]
        val next = states[i - 1]

        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableAngularVelocity = sqrt(current.w.squared + 2 * maxAngularAcceleration * angle)

        // Limit velocity based on reverse acceleration
        next.w = minOf(next.w, maxReachableAngularVelocity)

        // Calculate the reverse dt
        current.t = maxOf(current.t, (2 * angle) / (current.w + next.w))
    }
}

/**
 * Accumulative Pass for calculating higher-order derivatives
 * (acceleration and jerk), as well as giving back the sign
 * of the curvature
 */
private fun accumulativePass(states: List<TrajectoryState>) {
    for (i in 0 until states.size - 1) {
        val current = states[i + 1]
        val last = states[i]

        // Calculate angular acceleration
        current.dw = (current.w - last.w) / current.t

        // Calculate jerk
        current.ddw = (current.dw - last.dw) / current.t
    }
}