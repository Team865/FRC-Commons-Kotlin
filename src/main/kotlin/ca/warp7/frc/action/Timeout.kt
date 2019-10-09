package ca.warp7.frc.action

class Timeout(private val action: Action, private val timeout: Double) : Action {

    private var startTime = 0.0
    private var overtime = false

    override fun firstCycle() {
        startTime = System.nanoTime() / 1E9
        action.firstCycle()
    }

    override fun update() {
        if ((System.nanoTime() / 1E9 - startTime) > timeout) {
            overtime = true
            action.interrupt()
        } else {
            action.update()
        }
    }

    override fun shouldFinish(): Boolean {
        return overtime || action.shouldFinish()
    }

    override fun interrupt() {
        if (!overtime) {
            action.interrupt()
        }
    }

    override fun lastCycle() {
        if (!overtime) {
            action.lastCycle()
        }
    }
}