package ca.warp7.frc.action

fun executeUnrestricted(action: Action) {
    action.firstCycle()
    while (!action.shouldFinish()) {
        action.update()
        Thread.sleep(20)
    }
    action.lastCycle()
}