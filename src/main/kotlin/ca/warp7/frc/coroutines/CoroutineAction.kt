package ca.warp7.frc.coroutines

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionDSL
import ca.warp7.frc.action.runOnce

@ExperimentalCoroutineAction
@ActionDSL
fun coroutineAction(
        name: String = "Coroutine Action",
        block: suspend CoroutineActionScope<Unit>.() -> Unit
): Action {

    return object : Action {
        override fun name(): String {
            return name
        }
    }
}