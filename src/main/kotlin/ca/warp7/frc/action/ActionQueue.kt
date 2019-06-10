package ca.warp7.frc.action

interface ActionQueue : ActionDSLBase {
    @ActionDSL
    operator fun Action.unaryPlus()
}