@file:Suppress("unused")

package ca.warp7.frc.action


/**
 * Returns an action that runs other actions sequentially
 */
fun sequential(block: ActionBuilder.() -> Unit): Action = Sequential().apply(block)

/**
 * Returns an action that runs other actions in parallel
 */
fun parallel(block: ActionBuilder.() -> Unit): Action = Parallel().apply(block)

/**
 * Returns an action that does nothing until a condition is met
 */
fun waitUntil(predicate: (elapsed: Double) -> Boolean): Action = WaitUntil(predicate)

/**
 * Returns an action that runs only once
 */
inline fun runOnce(crossinline block: () -> Unit) = object : Action {
    override fun name(): String {
        return "runOnce"
    }

    override fun firstCycle() = block()
}

/**
 * Returns an action that gets called periodically forever
 */
inline fun periodic(crossinline block: () -> Unit) = object : Action {
    override fun name(): String {
        return "periodic"
    }

    override fun update() = block()
}

/**
 * Returns an action that does nothing for [seconds] seconds
 */
fun wait(seconds: Number) = waitUntil { elapsed -> elapsed > seconds.toDouble() }