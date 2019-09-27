package ca.warp7.frc.drive.trajectory

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.path.mixParameterizedPathOf
import ca.warp7.frc.toInt
import java.util.concurrent.FutureTask

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryController(builder: TrajectoryBuilder.() -> Unit) {

    private val builder = TrajectoryBuilder(builder)

    var t = 0.0
        private set

    var trajectory: List<TrajectoryState> = listOf()
        private set

    var totalTime = 0.0
        private set

    var initialState: Pose2D = Pose2D.identity
        private set

    private var trajectoryGenerator: FutureTask<List<TrajectoryState>>? = null

    private var invertMultiplier = 0.0
    private var mirroredMultiplier = 0.0

    fun setInverted(inverted: Boolean) {
        invertMultiplier = inverted.toInt().toDouble()
    }

    fun setMirrored(mirrored: Boolean) {
        mirroredMultiplier = mirrored.toInt().toDouble()
    }

    private fun <T> getArray(vararg t: T): Array<out T> {
        return t
    }

    fun initTrajectory(
            waypoints: Array<Pose2D>,
            absolute: Boolean,
            optimizeDkSquared: Boolean,
            robotState: Pose2D
    ) {
        val generator = FutureTask generator@{
            val startTime = System.nanoTime()
            val path = if (absolute) getArray(robotState, *waypoints) else waypoints
            val parameterizedPath = mixParameterizedPathOf(path,
                    optimizePath = optimizeDkSquared, bendFactor = builder.bendFactor)
            val b = generateTrajectory(parameterizedPath, builder.wheelbaseRadius,
                    builder.trajectoryVelocity,
                    builder.trajectoryAcceleration, builder.maxCentripetalAcceleration, builder.maxJerk)
            val elapsedTime = ((System.nanoTime() - startTime) / 1E6).toInt()
            println("Trajectory Generation Time: $elapsedTime ms")
            return@generator b
        }
        trajectoryGenerator = generator
        val thread = Thread(generator)
        thread.isDaemon = true
        thread.priority = Thread.NORM_PRIORITY
        thread.start()
    }

    fun tryFinishGeneratingTrajectory(): Boolean {
        val generator = trajectoryGenerator
        if (generator == null || !generator.isDone) return false // Check if generator is done generating
        trajectory = generator.get()
        totalTime = trajectory.last().t // reset tracking state
        t = 0.0
        trajectoryGenerator = null
        return true
    }

    fun updateInitialState(robotState: Pose2D) {
        val firstState = trajectory.first().arcPose
        initialState = Pose2D((robotState.translation - firstState.translation).rotate(-firstState.rotation),
                robotState.rotation - firstState.rotation)
    }

    fun getInitialToRobot(robotState: Pose2D): Pose2D {
        return Pose2D((robotState.translation - initialState.translation)
                .rotate(-initialState.rotation), robotState.rotation - initialState.rotation)
    }

    fun getError(robotState: Pose2D, setpoint: ArcPose2D): Pose2D {
        val initialToRobot = getInitialToRobot(robotState)
        return Pose2D((setpoint.translation - initialToRobot.translation)
                .rotate(-initialToRobot.rotation), setpoint.rotation - initialToRobot.rotation)
    }

    fun interpolatedTimeView(t: Double): TrajectoryState {
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]
        val x = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)

        val v = invertMultiplier * linearInterpolate(last.v, next.v, x)
        val dv = invertMultiplier * linearInterpolate(last.dv, next.dv, x)
        val w = mirroredMultiplier * linearInterpolate(last.w, next.w, x)
        val dw = mirroredMultiplier * linearInterpolate(last.dw, next.dw, x)

        val curvature = linearInterpolate(last.arcPose.curvature, next.arcPose.curvature, x) * mirroredMultiplier

        val position = last.arcPose.translation.interpolate(next.arcPose.translation, x)

        val heading = last.arcPose.rotation.interpolate(next.arcPose.rotation, x)
                .translation.scaled(invertMultiplier).direction

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