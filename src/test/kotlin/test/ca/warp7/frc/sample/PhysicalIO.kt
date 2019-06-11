package test.ca.warp7.frc.sample

import ca.warp7.frc.control.RobotController

class PhysicalIO : BaseIO {
    override var pushing: Boolean
        get() = TODO("not implemented")
        set(value) {}
    override var grabbing: Boolean
        get() = TODO("not implemented")
        set(value) {}

    override fun initialize() {
        TODO("not implemented")
    }

    override fun enable() {
        TODO("not implemented")
    }

    override fun disable() {
        TODO("not implemented")
    }

    override fun readInputs() {
        TODO("not implemented")
    }

    override fun writeOutputs() {
        TODO("not implemented")
    }

    override val driverInput: RobotController
        get() = TODO("not implemented")
    override val operatorInput: RobotController
        get() = TODO("not implemented")
}