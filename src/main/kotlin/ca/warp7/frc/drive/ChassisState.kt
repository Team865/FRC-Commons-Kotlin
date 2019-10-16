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
     * @param wheelbaseRadius the wheel base radius
     * @return the wheel state
     */
    fun solve(wheelbaseRadius: Double): WheelState = WheelState(
            left = linear - angular * wheelbaseRadius,
            right = linear + angular * wheelbaseRadius
    )
}