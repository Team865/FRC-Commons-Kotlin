package ca.warp7.frc.action

@ActionDSL
interface ActionDSLBase {
    @ActionDSL
    fun printTaskGraph()
    @ActionDSL
    fun onStart(block: ActionState.() -> Unit)
    @ActionDSL
    fun finishWhen(block: ActionState.() -> Boolean)
    @ActionDSL
    fun onUpdate(block: ActionState.() -> Unit)
    @ActionDSL
    fun onStop(block: ActionState.() -> Unit)
    @ActionDSL
    operator fun String.not()
}