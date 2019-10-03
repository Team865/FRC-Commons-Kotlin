package ca.warp7.frc.action

class ActionQueueImpl : ActionDSLImpl(), ActionQueue {

    private val queue: MutableList<Action> = mutableListOf()
    private var currentAction: Action? = null
    private var started = false

    override fun shouldFinish(): Boolean {
        return queue.isEmpty() && currentAction == null
    }

    override operator fun Action.unaryPlus() {
        if (!started) queue.add(this)
    }

    override fun firstCycle() {
        super.firstCycle()
        started = true
    }

    override fun update() {
        super.update()
        if (currentAction == null) {
            if (queue.isEmpty()) return
            val action = queue.removeAt(0)
            action.firstCycle()
            currentAction = action
        }
        currentAction?.update()
        if (currentAction?.shouldFinish() == true) {
            currentAction?.lastCycle()
            currentAction = null
        }
    }

    override fun lastCycle() {
        currentAction?.lastCycle()
    }

    override fun interrupt() {
        currentAction?.interrupt()
    }
}