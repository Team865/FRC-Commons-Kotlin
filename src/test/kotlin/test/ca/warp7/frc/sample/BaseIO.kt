package test.ca.warp7.frc.sample

import ca.warp7.frc.control.RobotController

interface BaseIO {
    fun initialize()
    fun enable()
    fun disable()

    val driverInput: RobotController
    val operatorInput: RobotController

    var pushing: Boolean
    var grabbing: Boolean

    fun readInputs()
    fun writeOutputs()
}
