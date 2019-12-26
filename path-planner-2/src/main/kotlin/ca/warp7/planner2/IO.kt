@file:Suppress("unused", "UNUSED_PARAMETER")

package ca.warp7.planner2

import ca.warp7.frc.f
import ca.warp7.frc.geometry.Pose2D
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.nio.file.Files
import java.nio.file.Path

fun loadPlannerState(path: Path): State {
    val parser = Parser.default()
    val result = parser.parse(Files.newInputStream(path)) as JsonObject
    TODO()
}

fun savePlannerState(path: Path, state: State) {
    TODO()
}

private fun pose2dToJava(pose: Pose2D): String {
    return "new Pose2D(${pose.translation.x.f}, ${pose.translation.y.f}, Rotation2D.fromDegrees(${pose.rotation.degrees().f}))"
}

private fun segmentToCommonsCommand(state: State, segment: Segment): String {
    val points2J = segment.waypoints.subList(1, segment.waypoints.size)
            .joinToString("\n") {
                ".moveTo(${pose2dToJava(it)})"
            }
    /*
    .setMaxVelocity(${state.config.maxVelocity}*${state.maxVRatio})
        .setMaxAcceleration(${state.config.maxAcceleration} * ${state.maxARatio})
        .setMaxCentripetalAcceleration(${state.config.maxCentripetalAcceleration}*${state.maxAcRatio})
     */
    return """    new DriveTrajectoryCommand(new SimpleFollower(), builder -> builder
        .startAt(${pose2dToJava(segment.waypoints.first())})
        $points2J
    )"""
}

fun toCommonsCommand(state: State): String {
    if (state.segments.size == 1) {
        val cmd = segmentToCommonsCommand(state, state.segments.first())
        return "$cmd;"
    }
    return state.segments.joinToString(",\n", "new SequentialCommandGroup(\n", "\n);") {
        segmentToCommonsCommand(state, it)
    }
}