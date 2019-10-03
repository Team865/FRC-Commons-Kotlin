package ca.warp7.frc.action

class RunOnce(private val func: () -> Unit) : Action {
    override fun firstCycle() {
        func()
    }

    override fun shouldFinish(): Boolean {
        return true
    }
}