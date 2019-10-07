package ca.warp7.frc.coroutines

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionDSL

@ExperimentalCoroutineAction
@ActionDSL
fun coroutineAction(
        name: String = "Coroutine Action",
        timing: () -> Double = { System.nanoTime() / 1E9 },
        func: suspend CoroutineActionScope<Unit>.() -> Unit
): Action = CoroutineAction(initialState = Unit, name = name, timing = timing, func = func)

@ExperimentalCoroutineAction
suspend fun CoroutineActionScope<*>.wait(seconds: Number) {
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
        func: suspend CoroutineActionScope<T>.() -> Unit
) = deferred(initialState, name, func).launch()


@ExperimentalCoroutineAction
suspend fun CoroutineActionScope<*>.step(action: Action) {
    action.firstCycle()
    while (true) {
        if (!nextCycle()) {
            break
        }
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