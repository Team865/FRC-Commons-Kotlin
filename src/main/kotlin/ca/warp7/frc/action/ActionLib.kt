@file:Suppress("unused")
@file:JvmName("ActionLib")

package ca.warp7.frc.action


/**
 * Returns an action that runs other actions sequentially
 */
@ActionDSL
fun sequential(block: ActionBuilder.() -> Unit): Action = Sequential().apply(block)

@Deprecated("queue() is renamed to sequential()", ReplaceWith("sequential(block)"))
fun queue(block: ActionBuilder.() -> Unit): Action = sequential(block)

/**
 * Returns an action that runs other actions in parallel
 */
@ActionDSL
fun parallel(block: ActionBuilder.() -> Unit): Action = Parallel().apply(block)

@Deprecated("async() is renamed to parallel()", ReplaceWith("parallel(block)"))
fun async(block: ActionBuilder.() -> Unit): Action = parallel(block)

/**
 * Returns an action that does nothing until a condition is met
 */
@ActionDSL
fun waitUntil(predicate: (elapsed: Double) -> Boolean): Action = WaitUntil(predicate)

/**
 * Returns an action that runs only once
 */
@ActionDSL
fun runOnce(block: () -> Unit) = RunOnce(block)

/**
 * Returns an action that gets called periodically forever
 */
@ActionDSL
inline fun periodic(crossinline block: () -> Unit) = object : Action {
    override fun name() = "periodic"
    override fun shouldFinish() = false
    override fun update() = block()
}

/**
 * Returns an action that does nothing for [seconds] seconds
 */
@ActionDSL
fun wait(seconds: Number) = waitUntil { elapsed -> elapsed > seconds.toDouble() }