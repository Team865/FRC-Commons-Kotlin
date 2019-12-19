package ca.warp7.planner2

import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.util.converter.NumberStringConverter
import java.lang.NumberFormatException

fun checkValidDouble(str: String, old: Double): Double {
    return try {
        str.trim().toDouble()
    } catch (e: NumberFormatException) {
        val dialog = Dialog<ButtonType>()
        dialog.title = "Settings Error"
        dialog.dialogPane.buttonTypes.add(ButtonType.OK)
        dialog.dialogPane.contentText = "Cannot parse $str, reverting to $old"
        old
    }
}

fun showSettings() {
    val config = Configuration() // todo
    val dialog = Dialog<ButtonType>()
    dialog.title = "Robot Settings"
    dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)


    val wheelbase = TextField()
    wheelbase.textFormatter = TextFormatter(NumberStringConverter())
    val maxVel = TextField()
    val maxAcc = TextField()
    val maxCA = TextField()
    val maxJ = TextField()
    val botWidth = TextField()
    val botLength = TextField()

    val choose = Button()
    val tangent = CheckBox()
    val angVel = CheckBox()

    dialog.dialogPane.content = GridPane().apply {
        hgap = 8.0
        vgap = 8.0
        add(Label("Effective Wheelbase Radius"), 0, 0)
        add(wheelbase, 1, 0)

        add(Label("Max Velocity"), 0, 1)
        add(maxVel, 1, 1)

        add(Label("Max Acceleration"), 0, 2)
        add(maxAcc, 1, 2)

        add(Label("Max Centripetal Acceleration"), 0, 3)
        add(maxCA, 1, 3)

        add(Label("Max Jerk"), 0, 4)
        add(maxJ, 1, 4)

        add(Label("Robot Width (for graphics)"), 0, 5)
        add(botWidth, 1, 5)

        add(Label("Robot Length (for graphics)"), 0, 6)
        add(botLength, 1, 6)

        add(Label("Half Field Background"), 2, 0)
        add(choose, 3, 0)

        add(Label("Show Tangent Circle"), 2, 1)
        add(tangent, 3, 1)

        add(Label("Show Angular Velocity Graph"), 2, 2)
        add(angVel, 3, 2)
    }

    dialog.showAndWait()

    config.wheelbaseRadius = checkValidDouble(wheelbase.text, config.wheelbaseRadius)
}