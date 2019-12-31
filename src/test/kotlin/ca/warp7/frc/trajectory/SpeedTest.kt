package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import kotlin.system.measureNanoTime

object SpeedTest {

    private fun bench() {
        val time = measureNanoTime {
            for (i in 5..500) {
                val path = parameterizedSplinesOf(
                        listOf(Pose2D(), Pose2D(2.0 * i, i.toDouble(), 0.0))
                )
                parameterizeTrajectory(path, 1.0, 3.0, 2.5,
                        20.0, Double.POSITIVE_INFINITY)
            }
        }
        println(time / 1E9)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        for (i in 0 until 10) bench()
    }
}
