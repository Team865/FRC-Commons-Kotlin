package test.ca.warp7.frc.sample

import ca.warp7.frc.action.IOAction

interface BaseIO: IOAction {
    fun initialize()
    fun enable()
    fun disable()
}
