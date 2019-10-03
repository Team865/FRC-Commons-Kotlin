package ca.warp7.frc.action

class ActionControl : Action {
    private var currentAction: Action? = null
    private var stopping = false

    fun setAction(action: Action) {
        currentAction = action
        stopping = false
    }

    override fun firstCycle() {
        currentAction?.firstCycle()
    }

    override fun update() {
        currentAction?.update()
    }

    override fun stop(interrupted: Boolean) {
        currentAction?.stop(interrupted)
        stopping = true
    }

    override fun lastCycle() {
        currentAction?.lastCycle()
        currentAction = null
    }

    override fun interrupt() {
        currentAction?.interrupt()
        currentAction = null
    }

    override fun shouldFinish(): Boolean {
        return stopping || currentAction?.shouldFinish() ?: true
    }

    fun flagAsDone() {
        stopping = true
    }
}