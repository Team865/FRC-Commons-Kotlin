package ca.warp7.frc.coroutines

import ca.warp7.frc.action.Action

@ExperimentalCoroutineAction
suspend fun CoroutineActionScope<*>.delay(seconds: Number) {
    val delaySeconds = seconds.toDouble()
    val startTime = elapsed()
    while (nextCycle()) {
        if ((elapsed() - startTime) > delaySeconds) {
            return
        }
    }
}

@ExperimentalCoroutineAction
fun <T> CoroutineActionScope<*>.launch(
        initialState: T,
        name: String = "",
        block: suspend CoroutineActionScope<T>.() -> Unit
) = deferred(initialState, name, block).launch()


@ExperimentalCoroutineAction
suspend fun CoroutineActionScope<*>.step(action: Action) {
    action.firstCycle()
    while (nextCycle()) {
        if (action.shouldFinish()) {
            action.lastCycle()
            return
        }
        action.update()
    }
    action.interrupt()
}


@ExperimentalCoroutineAction
suspend fun CoroutineActionScope<*>.steps(vararg actions: Action) {
    for (action in actions) {
        step(action)
    }
}