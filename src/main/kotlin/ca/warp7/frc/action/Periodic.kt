package ca.warp7.frc.action

class Periodic(private val func: () -> Unit) : Action {
    override fun shouldFinish(): Boolean {
        return false
    }

    override fun update() {
        func()
    }
}