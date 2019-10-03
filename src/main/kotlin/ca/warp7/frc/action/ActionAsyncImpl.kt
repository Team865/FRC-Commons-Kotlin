package ca.warp7.frc.action

internal class ActionAsyncImpl : ActionDSLImpl(), ActionAsyncGroup {
    override fun Action.unaryPlus() {
        actions.add(this)
    }


    private val actions = mutableListOf<Action>()

    override fun shouldFinish() = actions.all { it.shouldFinish() }

    override fun update() = actions.forEach { it.update() }

    override fun lastCycle() = Unit

    override fun interrupt() = actions.forEach { it.interrupt() }

    override fun firstCycle() = actions.forEach { it.firstCycle() }
}