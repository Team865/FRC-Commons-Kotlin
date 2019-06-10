@file:Suppress("unused")

package ca.warp7.frc.action

@ActionDSL
fun action(block: ActionDSLBase.() -> Unit): Action = ActionDSLImpl().apply(block)

@ActionDSL
fun async(block: ActionAsyncGroup.() -> Unit): Action = ActionAsyncImpl().apply(block)

@ActionDSL
fun queue(block: ActionQueue.() -> Unit): Action = ActionQueueImpl().apply(block)

@ActionDSL
fun mode(block: ActionDSLBase.() -> Unit): () -> Action = { action(block) }

@ActionDSL
fun await(action: Action) = action

@ActionDSL
fun waitUntil(predicate: ActionState.() -> Boolean) = action { finishWhen(predicate) }

@ActionDSL
fun wait(seconds: Int) = wait(seconds.toDouble())

@ActionDSL
fun wait(seconds: Double) = waitUntil { elapsed > seconds }

@ActionDSL
fun cleanup(block: ActionState.() -> Unit) = action { onStop(block) }

@ActionDSL
fun ActionDSLBase.runOnce(block: ActionState.() -> Unit) = action {
    onStart(block)
    finishWhen { true }
}

@ActionDSL
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

@ActionDSL
inline fun runOnce(crossinline block: () -> Unit) = object : Action {
    override fun start() = block()
    override val shouldFinish: Boolean get() = false
}

@ActionDSL
inline fun periodic(crossinline block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean get() = false
}

@ActionDSL
fun runAfter(seconds: Int, block: ActionState.() -> Unit) = runAfter(seconds.toDouble(), block)

@ActionDSL
fun runAfter(seconds: Double, block: ActionState.() -> Unit) = action {
    finishWhen { elapsed > seconds }
    onStop(block)
}