package ca.warp7.frc.drive

data class ChassisState(val linear: Double, val angular: Double) {
    override fun toString(): String {
        return "(${"%.3f".format(linear)}, ${"%.3f".format(angular)})"
    }


    /**
     * Solves the inverse kinematics of the drive train by converting linear
     * and angular speed/acceleration into the speeds/accelerations of wheels
     * on each side
     *
     * for velocity, solves (m/s, rad/s) into (m/s, m/s)
     * for acceleration, solves (m/s^2, rad/s^2) into (m/s^2, m/s^2)
     *
     * @param wheelbaseRadius the effective wheel base radius in m
     * @return the wheel state
     */
    fun solve(wheelbaseRadius: Double): WheelState = WheelState(
            left = linear - angular * wheelbaseRadius,
            right = linear + angular * wheelbaseRadius
    )

    /**
     * Solves the necessary torques on the left and right wheels
     * to produce a desired robot acceleration, which is a sum of
     * linear and angular acceleration forces, plus an angular drag
     * proportional to velocity.
     *
     * The result is multiplied by 0.5 because the forces needed by the
     * entire robot are distributed between the two wheels
     *
     * Then solve for input voltages based on velocity and torque
     *
     * Units for torque calculation:
     * m * (m/s^2 * kg - rad/s^2 * kg * m^2 / m - rad/s * ((N * m) / (rad/s)) / m)
     * = m * (m/s^2 * kg - 1/s^2 * kg * m^2 / m - 1/s * ((N * m) / (1/s)) / m)
     * = m * (N - N - N)
     * = N * m
     *
     * @param wheelRadius the radius of the wheel in m
     * @param wheelbaseRadius the effective wheel base radius in m
     * @param mass the linear inertia of the robot in kg
     * @param moi the moment of inertia of the robot in kg  * m^2
     * @return required torque for acceleration in (N * m, N * m)
     */
    fun solveTorque(
            wheelRadius: Double,
            wheelbaseRadius: Double,
            mass: Double,
            moi: Double
    ): WheelState = WheelState(
            left = 0.5 * wheelRadius * (linear * mass - angular * moi / wheelbaseRadius),
            right = 0.5 * wheelRadius * (linear * mass + angular * moi / wheelbaseRadius)
    )
}