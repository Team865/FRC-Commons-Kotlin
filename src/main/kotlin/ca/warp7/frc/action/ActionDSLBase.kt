package ca.warp7.frc.action


interface ActionDSLBase {
    fun printTaskGraph()
    fun onStart(block: ActionState.() -> Unit)
    fun finishWhen(block: ActionState.() -> Boolean)
    fun onUpdate(block: ActionState.() -> Unit)
    fun onStop(block: ActionState.() -> Unit)
}