@file:Suppress("unused")

package ca.warp7.frc.action

fun action(block: ActionDSLBase.() -> Unit): Action = ActionDSLImpl().apply(block)

fun async(block: ActionAsyncGroup.() -> Unit): Action = ActionAsyncImpl().apply(block)

fun queue(block: ActionQueue.() -> Unit): Action = ActionQueueImpl().apply(block)

fun mode(block: ActionDSLBase.() -> Unit): () -> Action = { action(block) }

fun await(action: Action) = action

fun waitUntil(predicate: ActionState.() -> Boolean) = action { finishWhen(predicate) }

fun wait(seconds: Int) = wait(seconds.toDouble())

fun wait(seconds: Double) = waitUntil { elapsed > seconds }

fun cleanup(block: ActionState.() -> Unit) = action { onStop(block) }

fun ActionDSLBase.runOnce(block: ActionState.() -> Unit) = action {
    onStart(block)
    finishWhen { true }
}

fun ActionDSLBase.periodic(block: ActionState.() -> Unit) = action {
    onUpdate(block)
    finishWhen { false }
}

//@ActionDSL
//fun Action.withTimeout(seconds: Double): Action {
//    val action = this
//    return action {
//        onStart { action.start() }
//        onUpdate { action.update() }
//        finishWhen { elapsed > seconds || action.shouldFinish }
//        onStop { action.stop() }
//    }
//}

inline fun runOnce(crossinline block: () -> Unit) = object : Action {
    override fun firstCycle() = block()
    override val shouldFinish: Boolean get() = false
}

inline fun periodic(crossinline block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean get() = false
}

fun runAfter(seconds: Int, block: ActionState.() -> Unit) = runAfter(seconds.toDouble(), block)

fun runAfter(seconds: Double, block: ActionState.() -> Unit) = action {
    finishWhen { elapsed > seconds }
    onStop(block)
}