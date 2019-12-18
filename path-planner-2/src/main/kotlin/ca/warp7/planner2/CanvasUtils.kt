package ca.warp7.planner2

import ca.warp7.frc.geometry.Translation2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.event.EventHandler as handler

interface CanvasScope {
    val canvas: Canvas

    fun keyPressed() {
    }

    fun keyReleased() {
    }

    fun mouseReleased() {
    }
}

fun CanvasScope.initCanvas() {
    canvas.onMouseMoved = handler {
    }
    canvas.onMouseReleased = handler {
        mouseReleased()
    }
    canvas.onMouseClicked = handler {
    }
    canvas.onKeyPressed = handler {
        keyPressed()
    }
    canvas.onKeyReleased = handler {
        keyReleased()
    }
    canvas.onMouseDragEntered = handler {
    }
    canvas.onMouseDragExited = handler {
    }
    canvas.onMouseDragOver = handler {
    }
    canvas.onMouseDragReleased = handler {
    }
}

inline fun CanvasScope.draw(action: GraphicsContext.() -> Unit) {
    canvas.graphicsContext2D.apply(action)
}


fun GraphicsContext.lineTo(a: Translation2D, b: Translation2D) = strokeLine(a.x, a.y, b.x, b.y)
fun GraphicsContext.vertex(a: Translation2D) = lineTo(a.x, a.y)
