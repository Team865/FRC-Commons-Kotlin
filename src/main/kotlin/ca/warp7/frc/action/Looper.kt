package ca.warp7.frc.action

import java.util.concurrent.ConcurrentHashMap

/**
 * Single-Instance scheduler for actions
 */
object Looper {

    private val actionLoops: MutableSet<Action> = ConcurrentHashMap.newKeySet()
    private val toRemove = mutableListOf<Action>()

    fun add(loop: Action) {
        loop.firstCycle()
        actionLoops.add(loop)
    }

    fun reset() {
        for (loop in actionLoops) loop.interrupt()
        actionLoops.clear()
    }

    fun update() {
        for (loop in actionLoops) {
            if (loop.shouldFinish()) {
                loop.lastCycle()
                toRemove.add(loop)
            } else {
                loop.update()
            }
        }
        if (toRemove.isNotEmpty()) {
            actionLoops.removeAll(toRemove)
            toRemove.clear()
        }
    }
}