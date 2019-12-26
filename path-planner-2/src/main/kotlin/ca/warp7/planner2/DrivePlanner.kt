package ca.warp7.planner2

import ca.warp7.frc.f2
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.linearInterpolate
import ca.warp7.planner2.fx.combo
import ca.warp7.planner2.fx.menuItem
import javafx.animation.AnimationTimer
import javafx.application.HostServices
import javafx.application.Platform
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate")
class DrivePlanner(val stage: Stage, hostServices: HostServices) {

    val ui = PlannerUI(stage)
    val dialogs = Dialogs(stage, hostServices)
    val gc: GraphicsContext = ui.canvas.graphicsContext2D

//    var draggingPoint = false
//    var draggingAngle = false

    val state = getDefaultState()
    val ref = state.reference
    val config = state.config

    var controlDown = false

    private val fileMenu = Menu("File", null,
            menuItem("Save as JSON", combo(KeyCode.S, control = true)) {

            },
            menuItem("Load JSON", combo(KeyCode.O, control = true)) {

            },
            menuItem("Configure Path", combo(KeyCode.COMMA, control = true)) {
                config.showSettings(stage)
                regenerate()
            },
            menuItem("Generate Commons-based Command", null) {
                val s = toCommonsCommand(state)
                dialogs.showTextBox("Command", s)
            },
            MenuItem("Generate Java Command"),
            MenuItem("Generate WPILib function"),
            MenuItem("Generate PathFinder-style CSV")
    )

    private val editMenu = Menu(
            "Edit",
            null,
            MenuItem("Insert Spline Control Point"),
            MenuItem("Insert Reverse Direction"),
            MenuItem("Insert Quick Turn"),
            MenuItem("Delete Point(s)"),
            MenuItem("Reverse Point(s)"),
            MenuItem("Snap Point(s) to 0.01m"),
            MenuItem("Edit Point"),
            MenuItem("Add Change to Point(s)"),
            menuItem("Mirror path about x axis", combo(KeyCode.M)) {

            }
    )

    private val pointMenu = Menu(
            "Control Point",
            null,
            menuItem("Rotate 1 degree counter-clockwise", combo(KeyCode.Q)) {

            },
            menuItem("Rotate 1 degree clockwise", combo(KeyCode.W)) {

            },
            menuItem("Move up 0.1 metres", combo(KeyCode.UP)) {
            },
            menuItem("Move down 0.1 metres", combo(KeyCode.DOWN)) {
            },
            menuItem("Move left 0.1 metres", combo(KeyCode.LEFT)) {
            },
            menuItem("Move right 0.1 metres", combo(KeyCode.RIGHT)) {
            },
            menuItem("Move forward 0.1 metres", combo(KeyCode.UP, shift = true)) {
            },
            menuItem("Move backward 0.1 metres", combo(KeyCode.DOWN, shift = true)) {
            },
            menuItem("Move left-normal 0.1 metres", combo(KeyCode.LEFT, shift = true)) {
            },
            menuItem("Move right-normal 0.1 metres", combo(KeyCode.RIGHT, shift = true)) {
            }
    )

    private val viewMenu = Menu("View", null,
            MenuItem("Resize Canvas to Window"),
            menuItem("Start/Pause Simulation", combo(KeyCode.SPACE)) { onSpacePressed() },
            menuItem("Stop Simulation", combo(KeyCode.DIGIT0)) { stopSimulation() }
    )

    init {
        ui.menuBar.menus.addAll(
                fileMenu,
                editMenu,
                pointMenu,
                viewMenu,
                dialogs.helpMenu
        )
        ui.canvas.setOnMouseClicked { onMouseClick(it.x, it.y) }
        stage.scene.setOnKeyPressed {
            if (it.isShortcutDown) controlDown = true
        }
        stage.scene.setOnKeyReleased {
            if (it.isShortcutDown) controlDown = false
        }
    }

    fun onMouseClick(x: Double, y: Double) {
        if (simulating) return
        val mouseOnField = ref.inverseTransform(Translation2D(x, y))

        if (mouseOnField.x > 0
                && mouseOnField.x < Constants.kFieldSize
                && mouseOnField.y > -Constants.kHalfFieldSize
                && mouseOnField.y < Constants.kHalfFieldSize) {
            var selectionChanged = false

            for (controlPoint in state.controlPoints) {
                if (!controlPoint.isSelected && controlPoint.pose.translation
                                .epsilonEquals(mouseOnField, Constants.kMouseControlPointRange)) {
                    controlPoint.isSelected = true
                    selectionChanged = true
                } else if (!controlDown && controlPoint.isSelected) {
                    controlPoint.isSelected = false
                    selectionChanged = true
                }
            }

            if (selectionChanged) {
                redrawScreen()
            }
        }
    }

    fun show() {
        Platform.runLater {
            regenerate()
            stage.show()
        }
    }

    fun regenerate() {
        state.generateAll()

        ui.pathStatus.putAll(mapOf(
                "∫(dξ)" to "${state.totalDist.f2}m",
                "∫(dt)" to "${state.totalTime.f2}s",
                "Σ(dCurvature²)" to state.totalSumOfCurvature.f2,
                "JerkLimit" to state.jerkLimiting.toString(),
                "Optimize" to state.optimizing.toString(),
                "MaxVel" to state.maxVelString(),
                "MaxAcc" to state.maxAccString(),
                "MaxCAcc" to state.maxAcString()
        ))
        ui.pointStatus.putAll(mapOf(
                "t" to "0.0s",
                "x" to "0.0m",
                "y" to "0.0m",
                "heading" to "0.0deg",
                "curvature" to "0.0rad/m",
                "v" to "0.0m/s",
                "ω" to "0.0rad/s",
                "dv/dt" to "0.0m/s^2",
                "dω/dt" to "0.0rad/s^2"
        ))
        redrawScreen()
    }

    fun redrawScreen() {
        val bg = state.config.background ?: return
        ref.set(bg.width, bg.height, 16.0, 16.0)

        ui.canvas.width = bg.width + 32 + 500
        ui.canvas.height = bg.height + 32
        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, ui.canvas.width, ui.canvas.height)
        gc.drawImage(bg, 16.0, 16.0)
        gc.drawImage(ui.referenceImage, bg.width + 32, 16.0, 96.0, 96.0)

        var j = 0
        for (i in state.segments.indices) {
            val t = state.segments[i]
            drawSplines(t, j % 2 == 1)
            if (t.trajectory.first().curvature.isFinite()) j++
        }
        drawAllControlPoints()
        drawGraph()
    }

    fun drawAllControlPoints() {
        if (simulating) return
        for (controlPoint in state.controlPoints) {
            gc.stroke = when {
                controlPoint.isSelected -> Color.rgb(0, 255, 255)
                controlPoint.indexInState % 2 == 1 -> Color.rgb(128, 255, 0)
                else -> Color.rgb(255, 255, 0)
            }
            drawArrowForPose(controlPoint.pose)
        }
    }

    fun drawArrowForPose(point: Pose2D) {
        val pos = ref.transform(point.translation)
        val heading = ref.transform(point.translation + point.rotation.translation().scaled(0.5))
        val dir = point.rotation.translation()

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

    fun drawSplines(segment: Segment, odd: Boolean) {

        gc.lineWidth = 1.5

        val s0 = segment.trajectory.first()
        val t0 = s0.pose.translation
        var normal = s0.pose.rotation.normal().translation() * config.robotWidth
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
            normal = s.pose.rotation.normal().translation() * config.robotWidth
            val newLeft = ref.transform(t - normal)
            val newRight = ref.transform(t + normal)

            if (s.curvature.isFinite()) {
                val kx = abs(s.curvature) / segment.maxCurvature
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

    fun drawGraph() {
        gc.lineWidth = 1.0
        gc.stroke = Color.rgb(255, 128, 0)
        var trackedTime = 0.0
        for (segment in state.segments) {
            for (trajectoryState in segment.trajectory) {
                val progress = (trajectoryState.t + trackedTime) / state.totalTime
                val x = (531 + progress * 474)
                gc.strokeLine(x, 335.0, x, 350.0)
            }
            trackedTime += segment.trajectoryTime
        }
        gc.stroke = Color.LIGHTGRAY
        gc.lineWidth = 1.0

        val accPxPerM = 50.0 / config.maxAcceleration
        val min = 384.0
        val max = 484.0
        gc.strokeLine(531.0, min, 531.0 + 474.0, min)
        gc.strokeLine(531.0, max, 531.0 + 474.0, max)
        var accStep = -config.maxAcceleration.toInt()
        while (accStep < config.maxAcceleration) {
            val h = min + (config.maxAcceleration - accStep) * accPxPerM
            gc.strokeLine(531.0, h, 531.0 + 474.0, h)
            accStep++
        }

        val velPxPerM = 50.0 / config.maxVelocity
        val min2 = 200.0
        val max2 = 300.0
        gc.strokeLine(531.0, min2, 531.0 + 474.0, min2)
        gc.strokeLine(531.0, max2, 531.0 + 474.0, max2)
        var velStep = -config.maxVelocity.toInt()
        while (velStep < config.maxVelocity) {
            val h = min2 + (config.maxVelocity - velStep) * velPxPerM
            gc.strokeLine(531.0, h, 531.0 + 474.0, h)
            velStep++
        }

        gc.lineWidth = 2.0
        gc.stroke = Color.rgb(0, 128, 192)
        trackedTime = 0.0
        gc.beginPath()
        for (segment in state.segments) {
            for (trajectoryState in segment.trajectory) {
                val progress = (trajectoryState.t + trackedTime) / state.totalTime
                val dv = if (segment.inverted) {
                    -trajectoryState.dv
                } else {
                    trajectoryState.dv
                }
                gc.lineTo(531 + progress * 474,
                        434 - dv / config.maxAcceleration * 50)
            }
            trackedTime += segment.trajectoryTime
        }
        gc.stroke()
        gc.lineWidth = 2.0
        gc.stroke = Color.rgb(128, 128, 255)
        trackedTime = 0.0
        gc.beginPath()
        for (segment in state.segments) {
            for (trajectoryState in segment.trajectory) {
                val progress = (trajectoryState.t + trackedTime) / state.totalTime
                val v = if (segment.inverted) {
                    -trajectoryState.v
                } else {
                    trajectoryState.v
                }
                gc.lineTo(531 + progress * 474, 250 - v / config.maxVelocity * 50)
            }
            trackedTime += segment.trajectoryTime
        }
        gc.stroke()
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

    fun onSpacePressed() {
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

    fun stopSimulation() {
        simulating = false
        simPaused = false
        redrawScreen()
        simulationTimer.stop()
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
            stopSimulation()
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
        val relativeTime = t - trackedTime

        val sample = simSeg.sample(relativeTime)

        val transformedPos = ref.transform(sample.pose.translation)
        redrawScreen()
        ui.pointStatus.putAll(mapOf(
                "t" to "${sample.t.f2}s",
                "x" to "${sample.pose.translation.x.f2}m",
                "y" to "${sample.pose.translation.y.f2}m",
                "heading" to "${sample.pose.rotation.degrees().f2}deg",
                "curvature" to "${sample.curvature.f2}rad/m",
                "v" to "${sample.v.f2}m/s",
                "ω" to "${sample.w.f2}rad/s",
                "dv/dt" to "${sample.dv.f2}m/s^2",
                "dω/dt" to "${sample.dw.f2}rad/s^2"
        ))
        drawRobot(transformedPos, sample.pose.rotation)
        gc.stroke = Color.WHITE
        drawArrowForPose(sample.pose)
        gc.stroke = Color.RED
        val x1 = (531 + (t / state.totalTime) * 474)
        gc.strokeLine(x1, 190.0, x1, 492.0)
    }
}