@file:JvmName("DifferentialDrive")

package ca.warp7.frc.drive

import kotlin.math.abs

/**
 * Solves the maximum forward velocity the robot can go on a curve,
 * given a measured max straight velocity. The parameters and
 * return values are unsigned. It must be multiplied by the
 * signed curvature to get the signed angular velocity
 *
 * Velocity constrained by these equations:
 * eqn 1. w = (right - left) / (2 * L)
 * eqn 2. v = (left + right) / 2
 *
 * 1. Rearrange equation 1:
 *        w(2 * L) = right - left;
 *        left = right - w(2 * L);
 * 2. Assuming the right side is at max velocity:
 *        right = V_max;
 *        left = V_max - w(2 * L)
 * 3. Substitute left and right into equation 2:
 *        v = (2 * V_max - w(2 * L)) / 2
 * 5. Substitute w = v * k into equation 2:
 *        v = (2 * V_max-v * k * 2 * L) / 2
 * 6. Rearrange to solve:
 *        v = V_max - v * k * L;
 *        v + v * k * L = V_max;
 *        v * (1 + k * L) = V_max;
 *        v = V_max / (1 + k * L);
 *
 * @param maxVelocity the maximum velocity when going straight in m/s
 * @param wheelbaseRadius the effective wheelbase radius in m
 * @param curvature the curvature of the path in m^-1
 */
fun maxVelocityAtCurvature(
        maxVelocity: Double,
        wheelbaseRadius: Double,
        curvature: Double
): Double = maxVelocity / (1 + abs(curvature) * wheelbaseRadius)