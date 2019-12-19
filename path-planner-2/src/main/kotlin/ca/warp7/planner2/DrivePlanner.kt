package ca.warp7.planner2

import ca.warp7.frc.f2
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.linearInterpolate
import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.Separator
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
        println("hi")

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
        for (segment in state.segments) {
            drawSplines(segment)
        }
    }

    fun drawSplines(segment: Segment) {

        gc.lineWidth = 1.5

        val s0 = segment.trajectory.first()
        val t0 = s0.pose.translation
        var normal = (s0.pose.rotation.normal() *
                config.robotWidth).translation()
        var left = ref.transform(t0 - normal)
        var right = ref.transform(t0 + normal)

        gc.stroke = Color.rgb(0, 255, 0)
        val a0 = ref.transform(t0) - ref.scale(Translation2D(config.robotLength,
                config.robotWidth).rotate(s0.pose.rotation))
        val b0 = ref.transform(t0) + ref.scale(Translation2D(-config.robotLength,
                config.robotWidth).rotate(s0.pose.rotation))
        gc.lineTo(a0, b0)
        gc.lineTo(left, a0)
        gc.lineTo(right, b0)

        for (i in 1 until segment.trajectory.size) {
            val s = segment.trajectory[i]
            val t = s.pose.translation
            normal = (s.pose.rotation.normal() * config.robotWidth).translation()
            val newLeft = ref.transform(t - normal)
            val newRight = ref.transform(t + normal)
            val kx = abs(s.curvature) / segment.maxK
            val r = linearInterpolate(0.0, 192.0, kx).toFloat() + 63
            val g = 255 - linearInterpolate(0.0, 192.0, kx).toFloat()
            gc.stroke = Color.rgb(r.toInt(), g.toInt(), 0)
            gc.lineTo(left, newLeft)
            gc.lineTo(right, newRight)
            left = newLeft
            right = newRight
        }

        val s1 = segment.trajectory.last()
        val t1 = s1.pose.translation

        gc.stroke = Color.rgb(0, 255, 0)
        val a1 = ref.transform(t1) - ref.scale(Translation2D(-config.robotLength,
                config.robotWidth).rotate(s1.pose.rotation))
        val b1 = ref.transform(t1) + ref.scale(Translation2D(config.robotLength,
                config.robotWidth).rotate(s1.pose.rotation))
        gc.lineTo(a1, b1)
        gc.lineTo(left, a1)
        gc.lineTo(right, b1)
    }
}