package ca.warp7.frc.action.coroutine

abstract class Action {


    open fun firstCycle() {}

    open fun update() {}

    open fun lastCycle() {}

    open fun stop() {}

    open fun shouldFinish(): Boolean {
        return false
    }

    fun runRoutine(routine: Routine) {

    }

    fun dispatch(debug: Boolean = false,
                 block: suspend DispatchScope.() -> Unit) {
    }

    private enum class State {
        FIRST_CYCLE, LAST_CYCLE, DONE, AWAIT_RESTART
    }

    private val coroutines: MutableList<Action> = mutableListOf()

    private var state = State.FIRST_CYCLE

    private fun nextState() {
    }

    private class Coroutine: DispatchScope {

        override suspend fun <T : Action> T.unaryPlus(): Dispatch<T> {
            TODO("not implemented")
        }

        override suspend fun await(vararg dispatch: Dispatch<*>) {
            TODO("not implemented")
        }

        override suspend fun cancel() {
            TODO("not implemented")
        }

        override suspend fun delay(seconds: Number): Dispatch<Action> {
            TODO("not implemented")
        }

        override suspend fun lock() {
            TODO("not implemented")
        }

        override suspend fun free() {
            TODO("not implemented")
        }

        override suspend fun parallel(block: suspend DispatchScope.() -> Unit) {
            TODO("not implemented")
        }
    }
}