package ca.warp7.frc.trajectory

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
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

    var initialState: Pose2D = Pose2D.identity
        private set

    private var trajectoryGenerator: FutureTask<List<TrajectoryState>>? = null

    var generationTimeMs = 0

    fun getFollower(): TrajectoryFollower? {
        return builder.follower
    }

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

    fun updateInitialState(robotState: Pose2D) {
        val firstState = trajectory.first().arcPose
        initialState = Pose2D((robotState.translation - firstState.translation).rotate(firstState.rotation.inverse),
                robotState.rotation - firstState.rotation)
    }

    fun getInitialToRobot(robotState: Pose2D): Pose2D {
        return Pose2D((robotState.translation - initialState.translation)
                .rotate(initialState.rotation.inverse), robotState.rotation - initialState.rotation)
    }

    fun getError(robotState: Pose2D, setpoint: ArcPose2D): Pose2D {
        val initialToRobot = getInitialToRobot(robotState)
        return Pose2D((setpoint.translation - initialToRobot.translation)
                .rotate(initialToRobot.rotation.inverse), setpoint.rotation - initialToRobot.rotation)
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
        val x = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)

        val v = builder.invertMultiplier * linearInterpolate(last.v, next.v, x)
        val dv = builder.invertMultiplier * linearInterpolate(last.dv, next.dv, x)
        val w = builder.mirroredMultiplier * linearInterpolate(last.w, next.w, x)
        val dw = builder.mirroredMultiplier * linearInterpolate(last.dw, next.dw, x)

        val curvature = linearInterpolate(last.arcPose.curvature, next.arcPose.curvature, x) *
                builder.mirroredMultiplier

        val position = last.arcPose.translation.interpolate(next.arcPose.translation, x)

        val heading = last.arcPose.rotation.interpolate(next.arcPose.rotation, x)
                .translation().scaled(builder.invertMultiplier.toDouble()).direction()

        val pose = ArcPose2D(Pose2D(position, heading), curvature, 0.0)

        return TrajectoryState(pose, v, w, dv, dw, 0.0, 0.0, t)
    }

    fun advanceTrajectory(dt: Double): TrajectoryState {
        t += dt
        return interpolatedTimeView(t)
    }

    fun isDoneTrajectory(): Boolean {
        return t > totalTime
    }
}