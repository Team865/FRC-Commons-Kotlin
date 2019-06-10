package ca.warp7.frc.action

interface ActionQueue : ActionDSL {
    operator fun Action.unaryPlus()
}