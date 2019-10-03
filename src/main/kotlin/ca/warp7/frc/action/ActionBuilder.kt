package ca.warp7.frc.action

@ActionDSL
interface ActionBuilder {

    /**
     * Adds an action to the manager scope
     */
    @ActionDSL
    operator fun Action.unaryPlus()
}