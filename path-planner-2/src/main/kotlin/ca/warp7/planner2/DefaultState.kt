package ca.warp7.planner2

import ca.warp7.frc.degrees
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import javafx.scene.image.Image
import java.io.FileInputStream

fun getDefaultState(): State {
    val state = State()

    state.config.apply {
        background = Image(FileInputStream("C:\\Users\\Yu\\IdeaProjects\\FRC-Commons-Kotlin\\path-planner\\src\\main\\resources\\field.PNG"))
        maxVelocity = 3.5
        maxAcceleration = 3.0
        maxCentripetalAcceleration = 4.0
        robotLength = 0.42
        robotWidth = 0.33
        wheelbaseRadius = 0.5
        maxJerk = 5.0
    }
    state.segments.add(Segment().apply {
        waypoints = listOf(
                Pose2D(1.829, 1.219, 0.degrees),
                Pose2D(5.120, 3.414, 32.degrees)
        )
    })
    val p = Pose2D(5.120, 3.414, (180 + 32).degrees) + Pose2D(2.3, -0.2, 0.0)
    state.segments.add(Segment().apply {
        inverted = true
        waypoints = listOf(
                Pose2D(5.120, 3.414, (180 + 32).degrees),
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
                Pose2D(0.914, 3.505, (-180).degrees)
        )
    })

    state.segments.add(Segment().apply {
        inverted = true
        waypoints = listOf(
                Pose2D(0.914, 3.505, 0.degrees),
                Pose2D(7.315, 2.133, 0.degrees)
        )
    })

    state.segments.add(Segment().apply {
        waypoints = listOf(
                Pose2D(7.315, 2.133, 180.degrees),
                Pose2D(7.315, 2.133, (180 - 90).degrees)
        )
    })

    state.segments.add(Segment().apply {
        waypoints = listOf(
                Pose2D(7.315, 2.133, (180 - 90).degrees),
                Pose2D(6.888, 3.413, (180 - 32).degrees)
        )
    })

    return state
}