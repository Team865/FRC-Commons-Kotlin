package ca.warp7.frc.action

interface ActionAsyncGroup : ActionDSL {
    operator fun Action.unaryPlus()
    val stopSignal: Action
}