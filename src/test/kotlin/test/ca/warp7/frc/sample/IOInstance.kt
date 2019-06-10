package test.ca.warp7.frc.sample

import edu.wpi.first.wpilibj.RobotBase

private val ioInstance = if (RobotBase.isReal()) PhysicalIO() else SimultatedIO()

fun ioInstance() = ioInstance