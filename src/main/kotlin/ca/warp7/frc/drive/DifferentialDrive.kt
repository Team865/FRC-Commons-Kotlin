@file:JvmName("DifferentialDrive")

package ca.warp7.frc.drive

import ca.warp7.frc.epsilonEquals
import kotlin.math.abs
import kotlin.math.withSign

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

/**
 * Solves the maximum reachable linear and angular velocity based on the curvature.
 *
 * The equations are derived from `w(r + L / 2) = far side velocity`. Assume far side goes 100%,
 * we replace it with max velocity and isolate for angular velocity.
 *
 * Then we rearrange `w = (right - left)/L` into `left = maxV - wL`, substitute it into
 * `v = (left + right) / 2`, and get the equation for max linear velocity
 *
 * L is double of wheelBaseRadius, so calculations are simplified here.
 *
 * If curvature is 0, it just returns a ChassisState with no angular velocity.
 *
 * Future: Does this also work for acceleration???
 *
 * @param curvature the curvature of the path in m^-1
 * @param maxVel the maximum velocity of the faster wheel in m/s
 * @param wheelbaseRadius the effective wheel base radius
 * @return the maximum reachable chassis velocity in (m/s, rad/s)
 */
@Deprecated(
        "Use better derived Equations",
        ReplaceWith(
                "maxVelocityAtCurvature(maxVel, wheelbaseRadius, curvature)" +
                        ".let { ChassisState(it, it * curvature) }"
        )
)
fun signedMaxVelocityAtCurvature(curvature: Double, maxVel: Double, wheelbaseRadius: Double): ChassisState {
    if (curvature.epsilonEquals(0.0, 1E-9)) {
        return ChassisState(maxVel, angular = 0.0)
    }
    val angular = maxVel / (1 / abs(curvature) + wheelbaseRadius)
    val linear = maxVel - (angular * wheelbaseRadius)
    return ChassisState(linear, angular.withSign(curvature))
}