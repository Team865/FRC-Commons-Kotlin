package ca.warp7.planner2

import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.util.*

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

    val statusMap: ObservableMap<String, String> = FXCollections.observableMap(TreeMap<String, String>())

    val statusLabel = Label().apply {
        style = "-fx-text-fill: white"
    }

    val statusBar = HBox().apply {
        style = "-fx-background-color: #1e2e4a"
        padding = Insets(4.0)
        children.add(statusLabel)
    }

    val view = BorderPane().apply {
        top = toolBar
        bottom = statusBar
    }

    fun showRobotSettings() {
        val dialog = Dialog<ButtonType>()
        dialog.title = "Robot Settings"
        dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)
        dialog.dialogPane.content = GridPane().apply {
            hgap = 8.0
            vgap = 8.0
            add(Label("Effective Wheelbase Radius"), 0, 0)
            add(TextField(), 1, 0)
            add(Label("Max Velocity"), 0, 1)
            add(TextField(), 1, 1)
            add(Label("Max Acceleration"), 0, 2)
            add(TextField(), 1, 2)
            add(Label("Max Centripetal Acceleration"), 0, 3)
            add(TextField(), 1, 3)
            add(Label("Max Jerk"), 0, 4)
            add(TextField(), 1, 4)
            add(Label("Robot Width (for graphics)"), 0, 5)
            add(TextField(), 1, 5)
            add(Label("Robot Length (for graphics)"), 0, 6)
            add(TextField(), 1, 6)
        }
        dialog.show()
    }

    fun show() {
        statusMap.addListener(MapChangeListener {
            statusLabel.text = statusMap.entries
                    .joinToString { it.key + ": " + it.value + "  " }
        })
        stage.scene = Scene(view)
        stage.title = "WARP7 PathPlanner"
        stage.icons.add(Image(DrivePlanner::class.java.getResourceAsStream("/icon.png")))
        stage.show()
    }
}