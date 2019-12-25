package ca.warp7.planner2

import ca.warp7.planner2.fx.combo
import ca.warp7.planner2.fx.menuItem
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Menu
import javafx.scene.input.KeyCode

class Dialogs  {
    private val shortcutButton = menuItem("Keyboard Shortcuts", combo(KeyCode.F1)) {
        val dialog = Dialog<ButtonType>()
        dialog.title = "Shortcuts"
        dialog.contentText = PlannerUI::class.java.getResourceAsStream("/docs.txt")
                .bufferedReader().readText()
        dialog.dialogPane.buttonTypes.add(ButtonType.OK)
        dialog.show()
    }

    private val aboutButton = menuItem("About", combo(KeyCode.F1, shift = true)) {
        val dialog = Dialog<ButtonType>()
        dialog.title = "About FRC Drive Trajectory Planner"
        dialog.contentText = "FRC Drive Trajectory Planner version 2019.9.0"
        dialog.dialogPane.buttonTypes.add(ButtonType.OK)
        dialog.show()
    }

    val helpMenu = Menu("Help", null, shortcutButton)
}