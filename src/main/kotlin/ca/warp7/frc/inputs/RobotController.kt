package ca.warp7.frc.inputs

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


    @Deprecated("'Axis' is removed", ReplaceWith("leftTrigger"))
    val leftTriggerAxis: Double
        get() = leftTrigger

    @Deprecated("'Axis' is removed", ReplaceWith("rightTrigger"))
    val rightTriggerAxis: Double
        get() = rightTrigger

    @Deprecated("'Axis' is removed", ReplaceWith("leftX"))
    val leftXAxis: Double
        get() = leftX

    @Deprecated("'Axis' is removed", ReplaceWith("leftY"))
    val leftYAxis: Double
        get() = leftY

    @Deprecated("'Axis' is removed", ReplaceWith("rightX"))
    val rightXAxis: Double
        get() = rightX

    @Deprecated("'Axis' is removed", ReplaceWith("rightY"))
    val rightYAxis: Double
        get() = rightY
}