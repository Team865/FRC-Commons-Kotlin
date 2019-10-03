package ca.warp7.frc.action

/**
 * An [Action] defines any self contained action that can be executed by a cycle-based
 * state machine, such as the event loop of a robot program. Actions are the basic units
 * for autonomous programs.
 *
 * Actions may contain anything, which means they can run sub-actions in various ways,
 * in combination with the [firstCycle], [update], [lastCycle], [interrupt], and
 * [shouldFinish] methods.
 *
 * An [Action] needs some sort of executor that defines what a "cycle" means. The
 * executor may be the main event loop on the robot, or it may be another "parent"
 * action or a coroutine.
 *
 * Methods of this interface have certain contracts that must be followed when
 * implementing action executors, which are documented for each method.
 *
 * An [Action] object is only meant to run once. The behaviour of the object
 * is unspecified if an executor attempts to run it again
 */

interface Action {

    /**
     * Returns the name of this action. It is the class name
     * by default
     */
    fun name(): String {
        return this::class.java.simpleName
    }

    /**
     * Run code once when the action is started
     *
     * [firstCycle] is called when this action is scheduled, as
     * determined by the executor of this action
     */
    fun firstCycle() {
        println("'${name()}' is started (first cycle); no override implementation")
    }


    @Deprecated(
            "start() is renamed to firstCycle()",
            ReplaceWith(expression = "firstCycle()")
    )
    fun start() {
        firstCycle()
    }


    /**
     * Returns whether or not the code has finished execution.
     *
     * [shouldFinish] is called once per cycle, and is called if
     * and only if [firstCycle] is called in a previous cycle.
     *
     * Previously, this returned true by default, but it has caused a lot of
     * confusion; so now every [Action] will continue to run by default
     */
    fun shouldFinish(): Boolean {
        return false
    }


    @Deprecated(
            "shouldFinish is no longer a property; it is now a boolean function",
            ReplaceWith(expression = "shouldFinish()")
    )
    val shouldFinish: Boolean
        get() = shouldFinish()


    /**
     * Updates the action in each cycle
     *
     * [update] is called once per cycle, if and only if [firstCycle]
     * has been called in a previous cycle and [shouldFinish] returns false
     * for the current cycle. If [shouldFinish] returns true, [update] may
     * not be called in subsequent cycles
     */
    fun update() {
    }


    /**
     * Run the last cycle of the action
     *
     * [lastCycle] is called if and only if [shouldFinish] has returned
     * true in the current cycle and [firstCycle] has been called in a previous
     * cycle
     */
    fun lastCycle() {
        println("'${name()}' is stopped (last cycle); no override implementation")
    }


    /**
     * Interrupt the action
     *
     * [interrupt] is called any time the action control manager
     * determines that the action should stop regardless of the condition
     * determined by [shouldFinish]. However, it may only be called after
     * [firstCycle] is called in a previous cycle. [interrupt] and
     * [lastCycle] are exclusive, meaning that they will never be both
     * called in the same action.
     *
     * [interrupt] will be used for ending timeout actions
     */
    fun interrupt() {
        println("'${name()}' is interrupted; no override implementation")
    }


    @Deprecated(
            "stop(Boolean) has been separated into lastCycle() and interrupt()",
            ReplaceWith(expression = "")
    )
    fun stop(interrupted: Boolean) {
        if (interrupted) {
            interrupt()
        } else {
            lastCycle()
        }
    }

    @Deprecated(
            "stop() has been renamed to lastCycle()",
            ReplaceWith(expression = "lastCycle()")
    )
    fun stop() {
    }
}