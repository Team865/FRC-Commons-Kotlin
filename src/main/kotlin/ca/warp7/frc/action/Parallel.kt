package ca.warp7.frc.action

internal class Parallel : Action, ActionBuilder {

    private val remaining = mutableListOf<Action>()
    private val toRemove = mutableListOf<Action>()

    override fun Action.unaryPlus() {
        remaining.add(this)
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