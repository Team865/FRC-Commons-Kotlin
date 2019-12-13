package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import kotlin.system.measureNanoTime

fun main() {
    for (i in 0 until 10) bench()
}

fun bench() {
//    var t1 = 0.0
    val time = measureNanoTime {
        for (i in 5..500) {
//            val b = System.nanoTime()
            val path = parameterizedSplinesOf(
                    listOf(Pose2D.identity, Pose2D(2.0 * i, i.toDouble(), 0.0))
            )
//            t1 += (System.nanoTime() - b) / 1E9
            parameterizeTrajectory(path, 1.0, 3.0, 2.5,
                    20.0, Double.POSITIVE_INFINITY)
        }
    }
//    println(t1)
    println(time / 1E9)
}