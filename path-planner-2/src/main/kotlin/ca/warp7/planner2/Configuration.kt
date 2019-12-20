package ca.warp7.planner2

import javafx.scene.image.Image

/**
 * The part of the State that the user sets in a
 * dialog box
 */
class Configuration {
    var wheelbaseRadius = 0.0
    var maxVelocity = 0.0
    var maxAcceleration = 0.0
    var maxCentripetalAcceleration = 0.0
    var maxJerk = Double.POSITIVE_INFINITY
    var robotWidth = 0.0
    var robotLength = 0.0
    var background: Image? = null
    var tangentCircle = false
    var angularGraph = false
}