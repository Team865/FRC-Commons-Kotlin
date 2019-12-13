package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.linearInterpolate
import java.util.concurrent.FutureTask

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryController(private val builder: TrajectoryBuilder) {

    var t = 0.0
        private set
    var trajectory: List<TrajectoryState> = listOf()
        private set
    var totalTime = 0.0
        private set
    private var trajectoryGenerator: FutureTask<List<TrajectoryState>>? = null
    var generationTimeMs = 0

    fun initTrajectory() {
        val generator = FutureTask {
            val startTime = System.nanoTime()
            val t = builder.generatePathAndTrajectory()
            generationTimeMs = ((System.nanoTime() - startTime) / 1E6).toInt()
            t
        }
        trajectoryGenerator = generator
        val thread = Thread(generator)
        thread.isDaemon = true
        thread.name = "Trajectory Generator"
        thread.start()
    }

    fun tryFinishGeneratingTrajectory(): Boolean {
        val generator = trajectoryGenerator
        if (generator == null || !generator.isDone) {
            return false // Check if generator is done generating
        }
        trajectory = generator.get()
        totalTime = trajectory.last().t
        t = 0.0
        trajectoryGenerator = null
        return true
    }

    /**
     * Get an interpolated view
     *
     * Assumes that points are close enough that everything is linear
     */
    fun interpolatedTimeView(t: Double): TrajectoryState {
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]
        val x = if (last.t == next.t) 1.0 else (t - last.t) / (next.t - last.t)

        val curvature = linearInterpolate(last.curvature, next.curvature, x)
        val position = last.pose.translation.interpolate(next.pose.translation, x)
        val heading = last.pose.rotation.interpolate(next.pose.rotation, x)
        val state = TrajectoryState(Pose2D(position, heading), curvature)

        state.v = linearInterpolate(last.v, next.v, x)
        state.dv = linearInterpolate(last.dv, next.dv, x)
        state.w = linearInterpolate(last.w, next.w, x)
        state.dw = linearInterpolate(last.dw, next.dw, x)
        state.t = t

        return if (builder.inverted) state.inverted() else state
    }

    fun advanceTrajectory(dt: Double): TrajectoryState {
        t += dt
        return interpolatedTimeView(t)
    }

    fun isDoneTrajectory(): Boolean {
        return t > totalTime
    }
}