package ca.warp7.frc.action

@ActionDSLMarker
interface ActionDSL {
    fun printTaskGraph()
    fun onStart(block: ActionState.() -> Unit)
    fun finishWhen(block: ActionState.() -> Boolean)
    fun onUpdate(block: ActionState.() -> Unit)
    fun onStop(block: ActionState.() -> Unit)
    operator fun String.not()
}