package ca.warp7.frc.action

open class ActionDSLImpl : ActionDSLBase, Action, ActionState {

    private var start: ActionState.() -> Unit = {}
    private var update: ActionState.() -> Unit = {}
    private var stop: ActionState.() -> Unit = {}
    private var predicate: ActionState.() -> Boolean = { true }

    override var name = ""
    private var startTime = 0.0

    override val elapsed get() = System.nanoTime() / 1e9 - startTime

    override fun firstCycle() {
        startTime = System.nanoTime() / 1e9
        start.invoke(this)
    }

    override fun shouldFinish(): Boolean {
        return predicate(this)
    }

    override fun update() {
        update.invoke(this)
    }

    override fun lastCycle() {
        stop.invoke(this)
    }

    override fun interrupt() {
        stop.invoke(this)
    }

    override fun onStart(block: ActionState.() -> Unit) {
        start = block
    }

    override fun finishWhen(block: ActionState.() -> Boolean) {
        predicate = block
    }

    override fun onUpdate(block: ActionState.() -> Unit) {
        update = block
    }

    override fun onStop(block: ActionState.() -> Unit) {
        stop = block
    }
}