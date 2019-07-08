package ca.warp7.frc.action.coroutine


internal class ManagedAction(val action: Action): Action() {

    override fun shouldFinish(): Boolean {
        return action.shouldFinish()
    }
}