package ca.warp7.planner2

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane

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