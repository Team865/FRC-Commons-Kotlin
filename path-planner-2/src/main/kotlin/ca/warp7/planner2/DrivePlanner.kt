package ca.warp7.planner2

import ca.warp7.frc.f2
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.linearInterpolate
import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.paint.Color
import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate")
class DrivePlanner {

    val ui = PlannerUI()
    val gc: GraphicsContext = ui.canvas.graphicsContext2D

    val pathActions = MenuButton(
            "Path Actions",
            null,
            MenuItem("Insert Spline Control Point"),
            MenuItem("Insert Reverse Direction"),
            MenuItem("Insert Quick Turn"),
            MenuItem("Delete Point(s)"),
            MenuItem("Reverse Point(s)"),
            MenuItem("Snap Point(s) to 0.01m"),
            MenuItem("Edit Point"),
            MenuItem("Add Change to Point(s)")
    )

    init {
        ui.toolBar.items.addAll(
                Button("Refit Canvas"),
                Separator(),
                pathActions,
                Separator(),
                Button("Simulate"),
                Separator(),
                MenuButton("Generate", null,
                        MenuItem("Java Trajectory Command"),
                        MenuItem("WPILib function"),
                        MenuItem("CSV File")
                ),
                Separator(),
                Button("Settings").apply {
                    setOnAction {
                        showSettings()
                    }
                },
                Separator(),
                ui.shortcutButton
        )
    }

    var selectedSegment = -1
    var selectedPoint = -1
    var selectionChanged = false

    var draggingPoint = false
    var draggingAngle = false
    var draggedControlPoint: Pose2D? = null

    val state = getDefaultState()
    val ref = state.reference
    val config = state.config

    fun show() {
        Platform.runLater {
            regenerate()
            ui.stage.show()
        }
    }


    fun regenerate() {

        val bg = state.config.background ?: return

        ui.canvas.width = bg.width + 32 + 300
        ui.canvas.height = bg.height + 32
        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, ui.canvas.width, ui.canvas.height)
        gc.stroke = Color.valueOf("#5a8ade")
        gc.lineWidth = 4.0
        gc.strokeRect(10.0, 10.0, bg.width + 12, bg.height + 12)
        gc.drawImage(bg, 16.0, 16.0)

        state.generateAll()

        gc.drawImage(ui.referenceImage, bg.width + 32, 16.0, 96.0, 96.0)
        ui.pathStatus.putAll(mapOf(
                "JerkLimit" to state.jerkLimiting.toString(),
                "Optimize" to state.optimizing.toString(),
                "B" to state.bendFactor.toString(),
                "MaxVel" to state.maxVelString(),
                "MaxAcc" to state.maxAccString(),
                "MaxCAcc" to state.maxAcSring(),
                "∫(dξ)" to "${state.totalDist.f2}m",
                "∫(dt)" to "${state.totalTime.f2}s",
                "Σ(dCurvature)²" to state.totalSumOfCurvature.f2
        ))
        ui.pointStatus.putAll(mapOf(
                "x" to "0.0m",
                "y" to "0.0m",
                "heading" to "0.0deg",
                "curvature" to "0.0rad/m",
                "t" to "0.0",
                "v" to "0.0m/s",
                "ω" to "0.0rad/s",
                "dv/dt" to "0.0m/s^2",
                "dω/dt" to "0.0rad/s^2"
        ))
        var j = 0
        for (i in state.segments.indices) {
            val t = state.segments[i]
            drawSplines(t, j % 2 == 1)
            if (t.trajectory.first().curvature.isFinite()) j++
        }
        j = 0
        for (i in state.segments.indices) {
            val t = state.segments[i]
            drawControlPoints(t, j % 2 == 1)
            if (t.trajectory.first().curvature.isFinite()) j++
        }

        val pl = ui.poseList
        pl.isShowRoot = false
        pl.root = TreeItem<Pose2D>().apply {
            children.addAll(state.segments.map {
                TreeItem<Pose2D>().apply {
                    children.addAll(it.waypoints.map { p -> TreeItem(p) })
                    isExpanded = true
                }
            })
        }
    }

    fun drawArrow(point: Pose2D) {
        val pos = ref.transform(point.translation)
        val heading = ref.transform(point.translation + point.rotation.translation().scaled(0.5))
        val dir = point.rotation.unit().translation()

        val r1 = dir.scaled(0.1524 * Constants.kTriangleRatio * 2)
        val r2 = r1.rotate(Rotation2D(0.0, 1.0)).scaled(Constants.kTriangleRatio)
        val r3 = r1.rotate(Rotation2D(0.0, -1.0)).scaled(Constants.kTriangleRatio)
        gc.strokeOval(pos.x - 6.0, pos.y - 6.0, 12.0, 12.0)
        val start = pos - dir.scaled(6.0).transposed()
        gc.lineTo(start, heading)
        val a1 = heading + ref.scale(r1)
        val a2 = heading + ref.scale(r2)
        val a3 = heading + ref.scale(r3)

        gc.beginPath()
        gc.vertex(a1)
        gc.vertex(a2)
        gc.vertex(a3)
        gc.vertex(a1)
        gc.closePath()
        gc.stroke()
    }

    fun drawControlPoints(segment: Segment, odd: Boolean) {
        if (odd) {
            gc.stroke = Color.rgb(128, 255, 0)
        } else {
            gc.stroke = Color.rgb(255, 255, 0)
        }
        for (point in segment.waypoints) {
            drawArrow(point)
        }
    }

    fun drawSplines(segment: Segment, odd: Boolean) {

        gc.lineWidth = 1.5

        val s0 = segment.trajectory.first()
        val t0 = s0.pose.translation
        var normal = (s0.pose.rotation.normal() *
                config.robotWidth).translation()
        var left = ref.transform(t0 - normal)
        var right = ref.transform(t0 + normal)

        if (s0.curvature.isFinite()) {
            if (odd) {
                gc.stroke = Color.rgb(0, 128, 255)
            } else {
                gc.stroke = Color.rgb(0, 255, 0)
            }
            val a0 = ref.transform(t0) - ref.scale(Translation2D(config.robotLength,
                    config.robotWidth).rotate(s0.pose.rotation))
            val b0 = ref.transform(t0) + ref.scale(Translation2D(-config.robotLength,
                    config.robotWidth).rotate(s0.pose.rotation))
            gc.lineTo(a0, b0)
            gc.lineTo(left, a0)
            gc.lineTo(right, b0)
        }

        for (i in 1 until segment.trajectory.size) {
            val s = segment.trajectory[i]
            val t = s.pose.translation
            normal = (s.pose.rotation.normal() * config.robotWidth).translation()
            val newLeft = ref.transform(t - normal)
            val newRight = ref.transform(t + normal)

            if (s.curvature.isFinite()) {
                val kx = abs(s.curvature) / segment.maxK
                if (odd) {
                    val r = linearInterpolate(0.0, 192.0, kx) + 63
                    val b = 255 - linearInterpolate(0.0, 192.0, kx)
                    gc.stroke = Color.rgb(r.toInt(), 128, b.toInt())
                } else {
                    val r = linearInterpolate(0.0, 192.0, kx) + 63
                    val g = 255 - linearInterpolate(0.0, 192.0, kx)
                    gc.stroke = Color.rgb(r.toInt(), g.toInt(), 0)
                }
            } else {
                gc.stroke = Color.MAGENTA
            }

            gc.lineTo(left, newLeft)
            gc.lineTo(right, newRight)
            left = newLeft
            right = newRight
        }

        val s1 = segment.trajectory.last()

        if (s1.curvature.isFinite()) {
            val t1 = s1.pose.translation
            if (odd) {
                gc.stroke = Color.rgb(0, 128, 255)
            } else {
                gc.stroke = Color.rgb(0, 255, 0)
            }
            val a1 = ref.transform(t1) - ref.scale(Translation2D(-config.robotLength,
                    config.robotWidth).rotate(s1.pose.rotation))
            val b1 = ref.transform(t1) + ref.scale(Translation2D(config.robotLength,
                    config.robotWidth).rotate(s1.pose.rotation))
            gc.lineTo(a1, b1)
            gc.lineTo(left, a1)
            gc.lineTo(right, b1)
        }

    }
}