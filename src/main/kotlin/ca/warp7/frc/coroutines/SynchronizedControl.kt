package ca.warp7.frc.coroutines

@ExperimentalActionDSL
class SynchronizedControl {
    private var currentAction = Action()

    fun setAction(action: Action) {
        synchronized(this) {
            currentAction = action
            currentAction.advanceState()
        }
    }

    fun interrupt() {
        synchronized(this) {
            currentAction.stop()
        }
    }

    fun updateActions() {
        synchronized(this) {
            currentAction.advanceState()
        }
    }
}