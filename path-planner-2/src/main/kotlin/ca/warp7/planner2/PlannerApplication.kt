package ca.warp7.planner2

import javafx.application.Application
import javafx.stage.Stage

class PlannerApplication : Application() {
    override fun start(primaryStage: Stage) {
        DrivePlanner(primaryStage, hostServices).show()
    }
}