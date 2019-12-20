package ca.warp7.planner2

import ca.warp7.frc.f2
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.linearInterpolate
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.input.KeyCode
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
        state.generateAll()

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
        redrawScreen()
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

    fun redrawScreen() {
        val bg = state.config.background ?: return

        ui.canvas.width = bg.width + 32 + 300
        ui.canvas.height = bg.height + 32
        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, ui.canvas.width, ui.canvas.height)
        gc.stroke = Color.valueOf("#5a8ade")
        gc.lineWidth = 4.0
        gc.strokeRect(10.0, 10.0, bg.width + 12, bg.height + 12)
        gc.drawImage(bg, 16.0, 16.0)
        gc.drawImage(ui.referenceImage, bg.width + 32, 16.0, 96.0, 96.0)

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

    var simFrameCount = 0

    val simulationTimer = object : AnimationTimer() {
        override fun handle(now: Long) {
            if (simFrameCount % 3 == 0) {
                handleSimulation()
            }
        }
    }

    var simulating = false
    var simPaused = false
    var simElapsed = 0.0
    var simElapsedChanged = false
    var lastTime = 0.0

    init {
        ui.stage.scene.setOnKeyPressed {
            if (it.code == KeyCode.SPACE) {
                if (simulating) {
                    simPaused = !simPaused
                } else {
                    simulating = true
                    simElapsed = 0.0
                    simFrameCount = 0
                    simPaused = false
                    lastTime = System.currentTimeMillis() / 1000.0
                    redrawScreen()
                    simulationTimer.start()
                }
            }
        }
    }

    fun drawRobot(pos: Translation2D, heading: Rotation2D) {
        val a = ref.scale(Translation2D(config.robotLength, config.robotWidth).rotate(heading))
        val b = ref.scale(Translation2D(config.robotLength, -config.robotWidth).rotate(heading))
        val p1 = pos + a
        val p2 = pos + b
        val p3 = pos - a
        val p4 = pos - b
        gc.stroke = Color.rgb(60, 92, 148)
        gc.fill = Color.rgb(90, 138, 222)
        gc.beginPath()
        gc.vertex(p1)
        gc.vertex(p2)
        gc.vertex(p3)
        gc.vertex(p4)
        gc.vertex(p1)
        gc.closePath()
        gc.stroke()
        gc.fill()
    }

    fun handleSimulation() {
        val nt = System.currentTimeMillis() / 1000.0
        val dt = nt - lastTime
        lastTime = nt
        if (simPaused) {
            if (!simElapsedChanged) return
            simElapsedChanged = false
        } else simElapsed += dt
        val t = simElapsed
        if (t > state.totalTime) {
            simulating = false
            simPaused = false
            redrawScreen()
            simulationTimer.stop()
            return
        }
        var trackedTime = 0.0
        var simSeg = state.segments.first()
        for (seg in state.segments) {
            if ((trackedTime + seg.trajectoryTime) > t) {
                simSeg = seg
                break
            }
            trackedTime += seg.trajectoryTime
        }
        var simIndex = -1
        val trajectory = simSeg.trajectory
        val relativeTime = t - trackedTime
        while (simIndex < trajectory.size - 2 && trajectory[simIndex + 1].t < relativeTime) {
            simIndex++
        }

        val thisMoment = trajectory[simIndex]
        val nextMoment = trajectory[simIndex + 1]
        val tx = (relativeTime - thisMoment.t) / (nextMoment.t - thisMoment.t)
        val pos = thisMoment.pose.translation.interpolate(nextMoment.pose.translation, tx)
        val heading = thisMoment.pose.rotation.interpolate(nextMoment.pose.rotation, tx)
        val transformedPos = ref.transform(pos)
        val curvature = linearInterpolate(thisMoment.curvature, nextMoment.curvature, tx)
        redrawScreen()
        if (curvature.isFinite() && curvature != 0.0 && config.tangentCircle) {
            val radius = 1 / curvature
            val offset = ref.scale(heading.normal().translation().scaled(radius))
            val center = transformedPos + offset
            val rad2 = ref.scale(radius) * 2
            gc.stroke = Color.YELLOW
            gc.strokeOval(center.x - rad2, center.y - rad2, rad2, rad2)
        }
        drawRobot(transformedPos, heading)
        gc.stroke = Color.WHITE
        drawArrow(Pose2D(pos, heading))
//        stroke(255f, 255f, 255f)
//        noFill()
//        val headingXY = pos + heading.translation().scaled(0.5).newXYNoOffset
//        val dir = heading.unit().translation()
//        drawArrow(ControlPoint(pos, headingXY, dir))
//        drawGraph(simIndex)
//        stroke(255f, 0f, 0f)
//        val x1 = (531 + (t / trajectoryTime) * 474)
//        line(x1, 233, x1, 492)
//        val x2 = (531 + (t / trajectoryTime) * 231)
//        line(x2, 17, x2, 225)
//        val x3 = (774 + (t / trajectoryTime) * 231)
//        line(x3, 17, x3, 225)
    }
}