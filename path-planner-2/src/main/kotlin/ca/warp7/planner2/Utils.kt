package ca.warp7.planner2

import ca.warp7.frc.geometry.Translation2D
import javafx.scene.canvas.GraphicsContext


val Double.f get() = String.format("%.3f", this)


fun GraphicsContext.lineTo(a: Translation2D, b: Translation2D) = strokeLine(a.x, a.y, b.x, b.y)
fun GraphicsContext.vertex(a: Translation2D) = lineTo(a.x, a.y)
