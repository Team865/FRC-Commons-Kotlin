package ca.warp7.planner2

import ca.warp7.frc.degrees
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import javafx.scene.image.Image
import java.io.FileInputStream

fun getDefaultState(): State {
    val state = State()

    state.config.apply {
        load()
        background = Image(FileInputStream("C:\\Users\\Yu\\IdeaProjects\\FRC-Commons-Kotlin\\path-planner\\src\\main\\resources\\field.PNG"))
        maxVelocity = 3.5
        maxAcceleration = 3.0
        maxCentripetalAcceleration = 4.0
        robotLength = 0.42
        robotWidth = 0.33
        wheelbaseRadius = 0.5
    }
    state.segments.add(Segment().apply {
        waypoints = listOf(
                Pose2D(6.feet, 4.feet, 0.degrees),
                Pose2D(16.8.feet, 11.2.feet, 32.degrees)
        )
    })
    val p = Pose2D(16.8.feet, 11.2.feet, (180 + 32).degrees) + Pose2D(2.3, -0.2, 0.0)
    state.segments.add(Segment().apply {
        inverted = true
        waypoints = listOf(
                Pose2D(16.8.feet, 11.2.feet, (180 + 32).degrees),
                p
        )
    })
    val p2 = Pose2D(p.translation, p.rotation + Rotation2D(-1.0, 0.0))
    state.segments.add(Segment().apply {
        waypoints = listOf(
                p2,
                p2 + Pose2D(0.0, 0.0, 110.degrees)
        )
    })
    state.segments.add(Segment().apply {
        waypoints = listOf(
                p2 + Pose2D(0.0, 0.0, 110.degrees),
                Pose2D(3.feet, 11.5.feet, (-180).degrees)
        )
    })

    state.segments.add(Segment().apply {
        inverted = true
        waypoints = listOf(
                Pose2D(3.feet, 11.5.feet, 0.degrees),
                Pose2D(24.feet, 8.feet, 0.degrees)
        )
    })

    state.segments.add(Segment().apply {
        waypoints = listOf(
                Pose2D(24.feet, 8.feet, 180.degrees),
                Pose2D(24.feet, 8.feet, (180 - 90).degrees)
        )
    })

    state.segments.add(Segment().apply {
        waypoints = listOf(
                Pose2D(24.feet, 8.feet, (180 - 90).degrees),
                Pose2D(22.6.feet, 11.2.feet, (180 - 32).degrees)
        )
    })

    return state
}