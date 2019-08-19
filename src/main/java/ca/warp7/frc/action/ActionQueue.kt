package ca.warp7.frc.action

interface ActionQueue : ActionDSLBase {
    operator fun Action.unaryPlus()
}