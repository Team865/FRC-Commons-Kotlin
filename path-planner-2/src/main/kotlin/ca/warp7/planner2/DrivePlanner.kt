package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D
import javafx.beans.property.SimpleStringProperty
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
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.io.FileInputStream

@Suppress("MemberVisibilityCanBePrivate")
class DrivePlanner {
    val stage = Stage()

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

    val toolBar = ToolBar().apply {
        items.addAll(
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

    val poseList = TreeTableView<Pose2D>().apply {
        columns.addAll(
                TreeTableColumn<Pose2D, String>("x").apply {
                    setCellValueFactory { SimpleStringProperty(it.value.value.translation.x.f) }
                },
                TreeTableColumn<Pose2D, String>("y").apply {
                    setCellValueFactory { SimpleStringProperty(it.value.value.translation.y.f) }
                },
                TreeTableColumn<Pose2D, String>("heading").apply {
                    setCellValueFactory { SimpleStringProperty(it.value.value.rotation.degrees().f) }
                }
        )
    }

    val canvas = Canvas()
    val gc = canvas.graphicsContext2D!!

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
        left = canvas
        right = sideBar
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


    fun show() {
        pathStatus.addListener(MapChangeListener {
            pathStatusLabel.text = pathStatus.entries
                    .joinToString("   ") { it.key + ": " + it.value }
        })
        pointStatus.addListener(MapChangeListener {
            pointStatusLabel.text = pointStatus.entries
                    .joinToString("   ") { it.key + ": " + it.value }
        })
        val bg = Image(FileInputStream("C:\\Users\\Yu\\IdeaProjects\\FRC-Commons-Kotlin\\path-planner\\src\\main\\resources\\field.PNG"))
        canvas.width = bg.width + 32 + 400
        canvas.height = bg.height + 32
        canvas.isFocusTraversable = true
        gc.fill = Color.WHITE
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)
        gc.stroke = Color.valueOf("#5a8ade")
        gc.lineWidth = 4.0
        gc.strokeRect(10.0, 10.0, bg.width + 12, bg.height + 12)
        gc.drawImage(bg, 16.0, 16.0)

        val ref = Image(DrivePlanner::class.java.getResourceAsStream("/reference.png"))
        gc.drawImage(ref, bg.width + 32, 16.0, 96.0, 96.0)
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