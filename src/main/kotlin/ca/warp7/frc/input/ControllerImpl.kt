package ca.warp7.frc.input

class ControllerImpl : RobotController {
    override var backButton = ButtonState.None
    override var aButton = ButtonState.None
    override var bButton = ButtonState.None
    override var xButton = ButtonState.None
    override var yButton = ButtonState.None
    override var leftBumper = ButtonState.None
    override var rightBumper = ButtonState.None
    override var leftStickButton = ButtonState.None
    override var rightStickButton = ButtonState.None
    override var startButton = ButtonState.None
    override var leftTrigger = 0.0
    override var rightTrigger = 0.0
    override var leftX = 0.0
    override var leftY = 0.0
    override var rightX = 0.0
    override var rightY = 0.0

    fun updateAxes(
            leftTrigger: Double,
            rightTrigger: Double,
            leftX: Double,
            leftY: Double,
            rightX: Double,
            rightY: Double
    ) {
        this.leftTrigger = leftTrigger
        this.rightTrigger = rightTrigger
        this.leftX = leftX
        this.leftY = leftY
        this.rightX = rightX
        this.rightY = rightY
    }
    
    @Suppress("DuplicatedCode")
    fun updateButtons(
            aButton: Boolean,
            bButton: Boolean,
            xButton: Boolean,
            yButton: Boolean,
            leftBumper: Boolean,
            rightBumper: Boolean,
            leftStickButton: Boolean,
            rightStickButton: Boolean,
            startButton: Boolean,
            backButton: Boolean
    ) {
        this.aButton = u(this.aButton, aButton)
        this.bButton = u(this.bButton, bButton)
        this.xButton = u(this.xButton, xButton)
        this.yButton = u(this.yButton, yButton)
        this.leftBumper = u(this.leftBumper, leftBumper)
        this.rightBumper = u(this.rightBumper, rightBumper)
        this.leftStickButton = u(this.leftStickButton, leftStickButton)
        this.rightStickButton = u(this.rightStickButton, rightStickButton)
        this.startButton = u(this.startButton, startButton)
        this.backButton = u(this.backButton, backButton)
    }

    fun reset() {
        leftTrigger = 0.0
        rightTrigger = 0.0
        leftX = 0.0
        leftY = 0.0
        rightX = 0.0
        rightY = 0.0
        aButton = ButtonState.None
        bButton = ButtonState.None
        xButton = ButtonState.None
        yButton = ButtonState.None
        leftBumper = ButtonState.None
        rightBumper = ButtonState.None
        leftStickButton = ButtonState.None
        rightStickButton = ButtonState.None
        startButton = ButtonState.None
        backButton = ButtonState.None
    }

    private fun u(old: ButtonState, _new: Boolean) =
            if (_new)
                if (old == ButtonState.Pressed || old == ButtonState.HeldDown)
                    ButtonState.HeldDown
                else
                    ButtonState.Pressed
            else
                if (old == ButtonState.Released || old == ButtonState.None)
                    ButtonState.None
                else
                    ButtonState.Released
}