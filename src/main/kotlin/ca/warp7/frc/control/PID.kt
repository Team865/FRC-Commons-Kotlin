package ca.warp7.frc.control

/**
 * PID Controller Values
 */
class PID(
        @JvmField var kP: Double = 0.0,
        @JvmField var kI: Double = 0.0,
        @JvmField var kD: Double = 0.0,
        @JvmField var kF: Double = 0.0
)