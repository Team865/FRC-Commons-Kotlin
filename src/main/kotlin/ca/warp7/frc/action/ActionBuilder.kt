package ca.warp7.frc.action

interface ActionBuilder {

    /**
     * Adds an action to the manager scope
     */
    operator fun Action.unaryPlus()
}