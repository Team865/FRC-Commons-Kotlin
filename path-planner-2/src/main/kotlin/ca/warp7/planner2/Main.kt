package ca.warp7.planner2

import javafx.application.Platform

fun main() {
    Platform.startup {
        DrivePlanner().show()
    }
}