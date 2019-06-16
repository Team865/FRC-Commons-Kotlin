package ca.warp7.frc.action

/**
 * An Action defines any self contained action that can be executed by the robot.
 * An Action is the unit of basis for autonomous programs. Actions may contain anything,
 * which means we can run sub-actions in various ways, in combination with the start,
 * update, end, and shouldFinish methods.
 */
interface Action {

    /**
     * Run code once when the action is started, usually for set up.
     * This method is called first before shouldFinish
     */
    fun start() {}

    fun initialize() { }

    /**
     * Returns whether or not the code has finished execution.
     */
    val shouldFinish: Boolean

    /**
     * Periodically updates the action
     */
    fun update() {}

    /**
     * Run code once when the action finishes, usually for clean up
     */
    fun stop() {}


    fun stop(interrupted: Boolean) {}
}