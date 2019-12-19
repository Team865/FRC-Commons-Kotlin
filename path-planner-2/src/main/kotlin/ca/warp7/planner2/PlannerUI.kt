package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.event.Event
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class PlannerUI {
    val stage = Stage()

    val toolBar = ToolBar()

    val poseList = TreeTableView<Pose2D>().apply {
        columns.addAll(
                TreeTableColumn<Pose2D, String>("x").apply {
                    isSortable = false
                    isResizable = false
                    setCellValueFactory {
                        SimpleStringProperty(it.value.value?.translation?.x?.f?.plus("m") ?: "---")
                    }
                },
                TreeTableColumn<Pose2D, String>("y").apply {
                    isSortable = false
                    isResizable = false
                    setCellValueFactory {
                        SimpleStringProperty(it.value.value?.translation?.y?.f?.plus("m") ?: "---")
                    }
                },
                TreeTableColumn<Pose2D, String>("heading").apply {
                    isSortable = false
                    isResizable = false
                    setCellValueFactory {
                        SimpleStringProperty(it.value.value?.rotation?.degrees()?.f?.plus("deg") ?: "---")
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

    val sideBar = poseList

    val view = BorderPane().apply {
        top = toolBar
        left = sideBar
        right = canvas
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

    val referenceImage = Image(DrivePlanner::class.java.getResourceAsStream("/reference.png"))

    val shortcutButton = Button("Shortcuts").apply {
        setOnAction {
            val dialog = Dialog<ButtonType>()
            dialog.title = "Shortcuts"
            dialog.contentText = PlannerUI::class.java.getResourceAsStream("/docs.txt")
                    .bufferedReader().readText()
            dialog.dialogPane.buttonTypes.add(ButtonType.OK)
            dialog.show()
        }
    }

    init {
        pathStatus.addListener(MapChangeListener {
            pathStatusLabel.text = pathStatus.entries
                    .joinToString("   ") { it.key + ": " + it.value }
        })
        pointStatus.addListener(MapChangeListener {
            pointStatusLabel.text = pointStatus.entries
                    .joinToString("   ") { it.key + ": " + it.value }
        })
        canvas.isFocusTraversable = true
        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED) { canvas.requestFocus() }
        stage.scene = Scene(view)
        stage.title = "WARP7 PathPlanner"
        stage.icons.add(Image(DrivePlanner::class.java.getResourceAsStream("/icon.png")))
    }
}