@file:JvmName("TrajectoryGenerator")

package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.ArcPose2D
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.squared
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

/**
 * Generates a list of timed [TrajectoryState] for a differential drive robot,
 * based on a list of [ArcPose2D] and drive train parameters.
 *
 * Formally, it generates a piecewise function T(t) that returns a [TrajectoryState],
 * with the maximum possible velocity, angular velocity, acceleration, and angular
 * acceleration for the elapsed time of the trajectory that does not violate the
 * limits of the differential drive math model, given a list of desired points.
 * The complexity based on path size is O(n).
 *
 * The input path of this function can be generated from points with
 * [ca.warp7.frc.path.parameterizedSplinesOf]. For the best results, the points should be
 * relatively close to one another.
 *
 * The path should not contain consecutive points on the same location because this algorithm
 * does not support turning in place (if that's included it would make the code more difficult
 * to read).
 *
 * Steps:
 * 1. Find the arc length between every consecutive point
 * 2. Perform a forward pass states to satisfy isolated and positive acceleration constraints
 * 3. Perform a reverse pass to satisfy negative acceleration constraints
 * 4. Perform an accumulative pass to calculate higher-order derivatives of velocity
 * 5. Apply a ramped acceleration pass to limit jerk
 * 6. Integrate dt of each state into total trajectory time
 *
 * @param path the target path of the trajectory.
 *
 * @param wheelbaseRadius the effect wheel base radius in metres
 *
 * @param maxVelocity the maximum linear velocity allowed in this trajectory in metres/second. It is
 * better to set this value to below the actual maximum of the robot for efficiency and accuracy.
 * (At most 80% of actual max velocity)
 *
 * @param maxAcceleration the maximum linear acceleration allowed in this trajectory in metres/second^2
 * Lower this for robot stability. However, this would usually increase total trajectory time more
 * significantly than [maxVelocity]
 *
 * @param maxCentripetalAcceleration the maximum centripetal acceleration in hertz. This value
 * can reduce maximum velocity at high curvatures and is useful to prevent tipping when the CoG is high
 *
 * @param maxJerk the maximum linear jerk allowed in this trajectory in metres/second^3. This value can
 * reduce spikes in voltages on the drive train to increase stability. [Double.POSITIVE_INFINITY] may
 * be passed instead to disable jerk limiting
 *
 * @see TrajectoryState
 * @see ArcPose2D
 *
 * @return a list of timed trajectory points
 */
fun generateTrajectory(
        path: List<ArcPose2D>, // (((x, y), θ), k, dk_ds)
        wheelbaseRadius: Double, // m
        maxVelocity: Double, // m/s
        maxAcceleration: Double, // m/s^2
        maxCentripetalAcceleration: Double, // s^-1
        maxJerk: Double // m/s^3
): List<TrajectoryState> {
    // If path is empty, returns an empty trajectory
    if (path.isEmpty()) {
        return emptyList()
    }

    // Make sure the first state doesn't have infinite curvature
    require(path.first().curvature.isFinite()) {
        "Infinite curvature is not allowed in the trajectory generator"
    }

    // Create result states
    val states = path.map { point -> TrajectoryState(point) }
    // Step 1
    val arcLengths = computeArcLengths(path)
    // Step 2
    forwardPass(states, arcLengths, wheelbaseRadius, maxVelocity, maxAcceleration, maxCentripetalAcceleration)
    // Step 3
    reversePass(states, arcLengths, maxAcceleration)
    // Step 4
    accumulativePass(states)
    // Step 5
    if (maxJerk.isFinite()) {
        rampedAccelerationPass(states, arcLengths, maxJerk)
    }
    // Step 6
    integrationPass(states)
    return states
}


/**
 * Compute arc length between each pair of poses in the path
 */
internal fun computeArcLengths(
        path: List<ArcPose2D> // (((x, y), θ), k, dk_ds)
): List<Double> {
    return path.zipWithNext { current, next ->

        // Check that the path is actually a  path
        require(!current.translation.epsilonEquals(next.translation)) {
            "Two points cannot contain the same translation in the trajectory generator"
        }

        // Get the magnitude of curvature on the next state
        // Note that the curvature is taken from the `next` state to predict
        // changing from and to infinity so that it doesn't result in a very
        // small arc length
        val k = abs(next.curvature)

        // Do not allow robot turning in place
        require(k.isFinite()) {
            "Infinite curvature is not allowed in the trajectory generator"
        }

        // Robot is moving in a curve or straight line
        // Returns the linear distance in metres

        // Get the chord length (translational distance)
        val distance = current.translation.distanceTo(next.translation)

        when {
            // Going straight, arcLength = distance
            k < 1E-6 -> distance

            // Moving on a radius:
            // arcLength = theta * r = theta / k
            // theta = asin(half_chord / r) * 2 = asin(half_chord * k) * 2
            // arcLength = asin(half_chord * k) * 2 / k
            else -> asin((distance / 2) * k) * 2 / k
        }
    }
}

/**
 * Forward pass
 */
private fun forwardPass(
        states: List<TrajectoryState>,
        arcLengths: List<Double>,
        wheelbaseRadius: Double,
        maxVelocity: Double,
        maxAcceleration: Double,
        maxCentripetalAcceleration: Double
) {

    // Assign the initial linear and angular velocity
    states.first().v = 0.0
    states.first().w = 0.0

    for (i in 0 until states.size - 1) {

        val arcLength = arcLengths[i]
        val current = states[i]
        val next = states[i + 1]

        val k = abs(next.arcPose.curvature)

        // Velocity constrained by these equations:
        // eqn 1. w = (right - left) / (2 * L)
        // eqn 2. v = (left + right) / 2
        //
        // 1. Rearrange equation 1:
        //        w(2 * L) = right - left;
        //        left = right - w(2 * L);
        // 2. Assuming the right side is at max velocity:
        //        right = V_max;
        //        left = V_max - w(2 * L)
        // 3. Substitute left and right into equation 2:
        //        v = (2 * V_max - w(2 * L)) / 2
        // 5. Substitute w = v * k into equation 2:
        //        v = (2 * V_max-v * k * 2 * L) / 2
        // 6. Rearrange to solve:
        //        v = V_max - v * k * L;
        //        v + v * k * L = V_max;
        //        v * (1 + k * L) = V_max;
        //        v = V_max / (1 + k * L);
        val driveKinematicConstraint = maxVelocity / (1 + k * wheelbaseRadius)

        // Velocity constrained inversely proportional to the curvature to slow down around turns
        val centripetalAccelerationConstraint = when {
            k > 1E-6 -> maxCentripetalAcceleration / k
            else -> maxVelocity
        }

        // Find the total constrained velocity
        val constrainedVelocity = minOf(driveKinematicConstraint, centripetalAccelerationConstraint)

        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableVelocity = sqrt(current.v.squared + 2 * maxAcceleration * arcLength)

        // Limit velocity based on curvature constraint and forward acceleration
        next.v = minOf(maxVelocity, constrainedVelocity, maxReachableVelocity)

        // Calculate the forward dt
        next.t = (2 * arcLength) / (current.v + next.v)
    }
}

/**
 * Reverse pass
 */
private fun reversePass(
        states: List<TrajectoryState>,
        arcLengths: List<Double>,
        maxAcceleration: Double
) {

    // Assign the final linear and angular velocity
    states.last().v = 0.0
    states.last().w = 0.0

    for (i in states.size - 1 downTo 1) {

        val arcLength = arcLengths[i - 1]
        val current = states[i]
        val next = states[i - 1]

        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableVelocity = sqrt(current.v.squared + 2 * maxAcceleration * arcLength)

        // Limit velocity based on reverse acceleration
        next.v = minOf(next.v, maxReachableVelocity)

        // Calculate the reverse dt
        current.t = maxOf(current.t, (2 * arcLength) / (current.v + next.v))
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

        // Calculate acceleration
        current.dv = (current.v - last.v) / current.t

        // Calculate angular velocity
        current.w = current.v * current.arcPose.curvature

        // Calculate angular acceleration
        current.dw = (current.w - last.w) / current.t

        // Calculate jerk
        current.ddv = (current.dv - last.dv) / current.t
        current.ddw = (current.dw - last.dw) / current.t
    }
}


/**
 * Limits jerk in a trajectory
 */
internal fun rampedAccelerationPass(
        states: List<TrajectoryState>,
        arcLengths: List<Double>,
        maxJerk: Double
) {

    // Find a list of points that exceeds the max jerk
    val jerkPoints = mutableListOf<Int>()

    for (i in 1 until states.size) {

        // Limit jerk at each of these points
        if (abs(states[i].ddv) > maxJerk) {
            jerkPoints.add(i)
        }
    }

    for (i in 0 until jerkPoints.size) {

        val stateIndex = jerkPoints[i]

        // Calculate a range of points to spread out the required acceleration
        val range = abs(states[stateIndex].ddv / (2 * maxJerk)).toInt() * 2 + 1

        // Calculate the bounds of the actual range with respect to other jerk points
        val start = maxOf(jerkPoints.getOrNull(i - 1) ?: 0, stateIndex - range)
        val end = minOf(jerkPoints.getOrNull(i + 1) ?: states.size - 1, stateIndex + range)

        val accStart = states[start].dv
        val accEnd = states[end].dv

        // Calculate the individual step size
        val maxTime = states.subList(start, end + 1).sumByDouble { it.t }
        var t = 0.0

        for (j in start..end) {

            val next = states[j + 1]
            val current = states[j]

            t += current.t
            val x = t / maxTime

            // Interpolate the acceleration
            current.dv = linearInterpolate(accStart, accEnd, x)

            // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
            val arcLength = arcLengths[j]

            val jerkLimitedVelocity = sqrt(current.v.squared + 2 * current.dv * arcLength)
            next.v = minOf(next.v, jerkLimitedVelocity)

            // Set the new dt
            next.t = maxOf(next.t, 2 * arcLength) / abs(current.v + next.v)
        }
    }
}

/**
 * Integrate dt into trajectory time
 */
internal fun integrationPass(
        states: List<TrajectoryState>
) {
    for (i in 1 until states.size) {
        states[i].t += states[i - 1].t
    }

    // Set the endpoints' acceleration and jerk to 0 to allow the motor to
    // stop if voltage is applied for torque
    states.first().apply {
        dv = 0.0
        dw = 0.0
        ddv = 0.0
        ddw = 0.0
    }
    states.last().apply {
        dv = 0.0
        dw = 0.0
        ddv = 0.0
        ddw = 0.0
    }
}