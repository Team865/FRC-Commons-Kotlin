package ca.warp7.frc.trajectory

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.path.*
import java.util.concurrent.FutureTask

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryController(
        private val builder: TrajectoryBuilder
) {

    var t = 0.0
        private set

    var trajectory: List<TrajectoryState> = listOf()
        private set

    var totalTime = 0.0
        private set

    var initialState: Pose2D = Pose2D.identity
        private set

    private var trajectoryGenerator: FutureTask<List<TrajectoryState>>? = null

    fun getFollower(): TrajectoryFollower? {
        return builder.follower
    }

    fun TrajectoryBuilder.generatePathAndTrajectory(): List<TrajectoryState> {
        val startTime = System.nanoTime()

        val trajectory = mutableListOf<TrajectoryState>()
        val path = mutableListOf<QuinticSegment2D>()

        for (i in 0 until waypoints.size - 1) {
            val a = waypoints[i]
            val b = waypoints[i + 1]
            if (!a.translation.epsilonEquals(b.translation)) {
                path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1], builder.bendFactor))
            } else {
                if (path.isNotEmpty()) {
                    val optimizedPath = if (builder.optimizeDkSquared) path.optimized() else path
                    val parameterizedPath = optimizedPath.parameterized()
                    val trajectorySegment = generateTrajectory(parameterizedPath, wheelbaseRadius,
                            trajectoryVelocity, trajectoryAcceleration, maxCentripetalAcceleration, maxJerk)
                    trajectory.addAll(trajectorySegment)
                    path.clear()
                }
                val theta = (b.rotation - a.rotation).radians
                val phi = a.rotation.radians
                require(theta != 0.0) {
                    "Trajectory Controller - Two points are the same"
                }
                val quickTurnPath = mutableListOf<ArcPose2D>()
                var x = 0.0
                if (theta > 0) {
                    while (x < theta) {
                        x += 0.1
                        quickTurnPath.add(ArcPose2D(Pose2D(a.translation,
                                Rotation2D.fromRadians(phi + x)), Double.POSITIVE_INFINITY, 0.0))
                    }
                } else {
                    while (x > theta) {
                        x -= 0.1
                        quickTurnPath.add(ArcPose2D(Pose2D(a.translation,
                                Rotation2D.fromRadians(phi + x)), Double.NEGATIVE_INFINITY, 0.0))
                    }
                }
                val quickTurnSegment = generateQuickTurn(quickTurnPath,
                        trajectoryVelocity / wheelbaseRadius,
                        trajectoryAcceleration / wheelbaseRadius)
                trajectory.addAll(quickTurnSegment)
            }
        }

        val elapsedTime = ((System.nanoTime() - startTime) / 1E6).toInt()
        println("Path Generation Time: $elapsedTime ms")

        return trajectory
    }

    fun initTrajectory() {
        val generator = FutureTask { builder.generatePathAndTrajectory() }
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
        totalTime = trajectory.last().t // reset tracking state
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
                .translation.scaled(builder.invertMultiplier).direction

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