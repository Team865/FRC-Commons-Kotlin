@file:JvmName("ActionLib")

package ca.warp7.frc.action

/**
 * Returns an action that runs other actions sequentially
 */
@ActionDSL
fun sequential(block: ActionBuilder.() -> Unit): Action = Sequential().apply(block)

/**
 * Returns an action that runs other actions in parallel
 */
@ActionDSL
fun parallel(block: ActionBuilder.() -> Unit): Action = Parallel().apply(block)

/**
 * Returns an action that does nothing until a condition is met
 */
@ActionDSL
fun waitUntil(predicate: (elapsed: Double) -> Boolean): Action = WaitUntil(predicate)


/**
 * Returns an action that does nothing for [seconds] seconds
 */
@ActionDSL
fun wait(seconds: Double) = waitUntil { elapsed -> elapsed > seconds }

@ActionDSL
fun wait(seconds: Number) = wait(seconds.toDouble())


/**
 * Returns an action that runs only once
 */
@ActionDSL
fun runOnce(block: () -> Unit) = RunOnce(block)


/**
 * Returns an action that gets called periodically forever
 */
@ActionDSL
fun periodic(block: () -> Unit) = Periodic(block)

/**
 * Returns an action wrapper that interrupts at a timeout
 */
@ActionDSL
fun Action.withTimeout(seconds: Double) = Timeout(this, seconds)