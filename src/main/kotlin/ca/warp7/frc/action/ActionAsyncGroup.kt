package ca.warp7.frc.action

interface ActionAsyncGroup : ActionDSLBase {
    @ActionDSL
    operator fun Action.unaryPlus()
    @ActionDSL
    val stopSignal: Action
}