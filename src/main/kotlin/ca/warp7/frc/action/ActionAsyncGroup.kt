package ca.warp7.frc.action

interface ActionAsyncGroup : ActionDSLBase {
    operator fun Action.unaryPlus()
    val stopSignal: Action
}