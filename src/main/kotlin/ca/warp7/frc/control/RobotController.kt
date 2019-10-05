package ca.warp7.frc.control

interface RobotController {
    val aButton: ButtonState
    val bButton: ButtonState
    val xButton: ButtonState
    val yButton: ButtonState
    val leftBumper: ButtonState
    val rightBumper: ButtonState
    val leftStickButton: ButtonState
    val rightStickButton: ButtonState
    val backButton: ButtonState
    val startButton: ButtonState
    val leftTriggerAxis: Double
    val rightTriggerAxis: Double
    val leftXAxis: Double
    val leftYAxis: Double
    val rightXAxis: Double
    val rightYAxis: Double
}