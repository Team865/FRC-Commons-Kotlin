package ca.warp7.planner2

import javafx.scene.control.*
import javafx.scene.layout.GridPane
import java.lang.NumberFormatException

fun validate(str: String, old: Double): Double {
    return try {
        str.trim().toDouble()
    } catch (e: NumberFormatException) {
        val dialog = Dialog<ButtonType>()
        dialog.title = "Settings Error"
        dialog.dialogPane.buttonTypes.add(ButtonType.OK)
        dialog.dialogPane.contentText = "Cannot parse $str as a number, reverting to $old"
        dialog.showAndWait()
        old
    }
}

fun showSettings() {
    val config = Configuration() // todo
    val dialog = Dialog<ButtonType>()
    dialog.title = "Robot Settings"
    dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)


    val wheelbase = TextField(config.wheelbaseRadius.toString())
    val maxVel = TextField(config.maxVelocity.toString())
    val maxAcc = TextField(config.maxAcceleration.toString())
    val maxCA = TextField(config.maxCentripetalAcceleration.toString())
    val maxJ = TextField(config.maxJerk.toString())
    val botWidth = TextField(config.robotWidth.toString())
    val botLength = TextField(config.robotLength.toString())

    val choose = Button("Choose")
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

    config.wheelbaseRadius = validate(wheelbase.text, config.wheelbaseRadius)
    config.maxVelocity = validate(maxVel.text, config.maxVelocity)
    config.maxAcceleration = validate(maxAcc.text, config.maxAcceleration)
    config.maxCentripetalAcceleration = validate(maxCA.text, config.maxCentripetalAcceleration)
    config.maxJerk = validate(maxJ.text, config.maxJerk)
    config.robotWidth = validate(botWidth.text, config.robotWidth)
    config.robotLength = validate(botLength.text, config.robotLength)
}