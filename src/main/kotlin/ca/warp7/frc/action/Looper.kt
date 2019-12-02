@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc.action

import java.util.concurrent.ConcurrentHashMap

/**
 * Single-Instance scheduler for actions
 */
object Looper {

    @JvmStatic
    private val actionLoops: MutableSet<Action> = ConcurrentHashMap.newKeySet()
    @JvmStatic
    private val toRemove = mutableListOf<Action>()

    @JvmStatic
    fun add(loop: Action) {
        loop.firstCycle()
        actionLoops.add(loop)
    }

    @JvmStatic
    fun remove(loop: Action) {
        actionLoops.remove(loop)
        loop.interrupt()
    }

    @Deprecated("", ReplaceWith("resetAll()"))
    fun reset() {
        resetAll()
    }

    @JvmStatic
    fun resetAll() {
        for (loop in actionLoops) {
            loop.interrupt()
        }
        actionLoops.clear()
    }

    fun printAll() {
        for ((index, loop) in actionLoops.withIndex()) {
            println("Loop $index - ${loop.name()}")
        }
    }

    @JvmStatic
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