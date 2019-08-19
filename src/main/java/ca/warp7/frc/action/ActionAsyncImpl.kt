package ca.warp7.frc.action

class ActionAsyncImpl : ActionDSLImpl(), ActionAsyncGroup {
    override fun Action.unaryPlus() {
        asyncActions.add(this)
    }

    override val stopSignal: Action
        get() = object : Action {
            override fun firstCycle() {
                isStopSignal = true
            }

            override val shouldFinish: Boolean
                get() = true
        }

    private val asyncActions = mutableListOf<Action>()
    private var isStopSignal = false

    override val shouldFinish: Boolean get() = isStopSignal || asyncActions.all { it.shouldFinish }
    override fun update() = asyncActions.forEach { it.update() }
    override fun stop(interrupted: Boolean) = asyncActions.forEach { it.stop(true) }
    override fun firstCycle() = asyncActions.forEach { it.firstCycle() }
}