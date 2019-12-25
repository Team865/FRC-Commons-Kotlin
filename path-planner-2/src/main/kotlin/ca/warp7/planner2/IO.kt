package ca.warp7.planner2

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.nio.file.Files
import java.nio.file.Path

fun loadPlannerState(path: Path): State {
    val parser = Parser.default()
    val result = parser.parse(Files.newInputStream(path)) as JsonObject
    TODO()
}

fun savePlannerState(path: Path, state: State) {
    TODO()
}