package ca.warp7.frc.action

class Sequential : Action, ActionBuilder {

    private val remaining: MutableList<Action> = mutableListOf()
    private var currentAction: Action? = null

    override fun shouldFinish(): Boolean {
        return remaining.isEmpty() && currentAction == null
    }

    override operator fun Action.unaryPlus() {
        remaining.add(this)
    }

    override fun firstCycle() {
        update()
    }

    override fun update() {
        super.update()
        if (currentAction == null) {
            if (remaining.isEmpty()) return
            val action = remaining.removeAt(0)
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