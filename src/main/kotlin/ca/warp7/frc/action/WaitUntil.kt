package ca.warp7.frc.action

class WaitUntil(private val predicate: (elapsed: Double) -> Boolean) : Action {

    var startTime = 0.0

    override fun firstCycle() {
        startTime = System.nanoTime() / 1E9
    }

    override fun shouldFinish(): Boolean {
        return predicate((System.nanoTime() / 1E9) - startTime)
    }
}