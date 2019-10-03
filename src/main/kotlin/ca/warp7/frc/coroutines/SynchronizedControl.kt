package ca.warp7.frc.coroutines

@ExperimentalCoroutineAction
class SynchronizedControl {
    private var currentAction = CoroutineAction()

    fun setAction(action: CoroutineAction) {
        synchronized(this) {
            currentAction = action
            currentAction.advanceState()
        }
    }

    fun interrupt() {
        synchronized(this) {
            currentAction.stop0()
        }
    }

    fun updateActions() {
        synchronized(this) {
            currentAction.advanceState()
        }
    }
}