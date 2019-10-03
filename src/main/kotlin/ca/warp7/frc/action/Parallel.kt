package ca.warp7.frc.action

class Parallel(vararg actions: Action) : Action, ActionBuilder {

    private val remaining = actions.toMutableList()
    private val toRemove = mutableListOf<Action>()

    override fun add(action: Action) {
        remaining.add(action)
    }

    override fun shouldFinish(): Boolean {
        for (action in remaining) {
            if (action.shouldFinish()) {
                toRemove.add(action)
            }
        }
        if (toRemove.isNotEmpty()) {
            remaining.removeAll(toRemove)
            toRemove.clear()
        }
        return remaining.isEmpty()
    }

    override fun update() = remaining.forEach { it.update() }

    override fun lastCycle() = remaining.forEach { it.lastCycle() }

    override fun interrupt() = remaining.forEach { it.interrupt() }

    override fun firstCycle() = remaining.forEach { it.firstCycle() }
}