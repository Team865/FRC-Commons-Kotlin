package ca.warp7.planner2

import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

@Suppress("MemberVisibilityCanBePrivate")
class DrivePlanner {
    val stage = Stage()

    val toolBar = ToolBar().apply {
        items.addAll(
                Button("Insert Point"),
                Button("Delete Point"),
                Button("Insert Quick Turn"),
                Button("Insert Direction Change"),
                Separator(),
                Button("Robot Settings").apply {
                    setOnAction {
                        showRobotSettings()
                    }
                },
                Button("Field Settings"),
                Separator(),
                Button("Simulate Trajectory"),
                Button("Show Graphs"),
                Button("Generate Java Code"),
                Separator(),
                Button("Shortcuts").apply {
                    setOnAction {
                        val dialog = Dialog<ButtonType>()
                        dialog.title = "Shortcuts"
                        dialog.contentText = DrivePlanner::class.java.getResourceAsStream("/docs.txt")
                                .bufferedReader().readText()
                        dialog.dialogPane.buttonTypes.add(ButtonType.OK)
                        dialog.show()
                    }
                }
        )
    }

    val canvas = Canvas()

    val pathStatus: ObservableMap<String, String> = FXCollections
            .observableMap<String, String>(LinkedHashMap())

    val pathStatusLabel = Label().apply {
        style = "-fx-text-fill: white"
    }

    val pointStatus: ObservableMap<String, String> = FXCollections
            .observableMap<String, String>(LinkedHashMap())

    val pointStatusLabel = Label().apply {
        style = "-fx-text-fill: white"
    }

    val view = BorderPane().apply {
        top = toolBar
        center = canvas
        bottom = VBox().apply {
            children.addAll(
                    HBox().apply {
                        style = "-fx-background-color: #3c5c94"
                        padding = Insets(4.0, 16.0, 4.0, 16.0)
                        children.add(pointStatusLabel)
                    },
                    HBox().apply {
                        style = "-fx-background-color: #1e2e4a"
                        padding = Insets(4.0, 16.0, 4.0, 16.0)
                        children.add(pathStatusLabel)
                    }
            )
        }
    }


    fun updateMainCanvas() {
        canvas.height = stage.height
        canvas.width = stage.height / 2.0 * 3.0
    }

    fun show() {
        pathStatus.addListener(MapChangeListener {
            pathStatusLabel.text = pathStatus.entries
                    .joinToString("   ") { it.key + ": " + it.value }
        })
        pointStatus.addListener(MapChangeListener {
            pointStatusLabel.text = pointStatus.entries
                    .joinToString("   ") { it.key + ": " + it.value }
        })
        pathStatus.putAll(mapOf(
                "JerkLimit" to "off",
                "Optimize" to "off",
                "B" to "1.2",
                "MaxVel" to "3.0m/s×1.0",
                "MaxAcc" to "3.0m/s^2×1.0",
                "MaxCAcc" to "3.0rad/s×1.0",
                "∫(dξ)" to "0.0m",
                "∫(dt)" to "0.0s",
                "Σ(dCurvature)²" to "0.0"
        ))
        pointStatus.putAll(mapOf(
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
        stage.scene = Scene(view)
        stage.title = "WARP7 PathPlanner"
        stage.icons.add(Image(DrivePlanner::class.java.getResourceAsStream("/icon.png")))
        stage.show()
    }
}