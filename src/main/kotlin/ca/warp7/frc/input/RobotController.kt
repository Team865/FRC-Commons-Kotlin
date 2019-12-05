package ca.warp7.frc.input

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

    val leftTrigger: Double
    val rightTrigger: Double

    val leftX: Double
    val leftY: Double

    val rightX: Double
    val rightY: Double
}