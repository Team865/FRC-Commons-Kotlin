@file:Suppress("unused")
@file:JvmName("SplinePaths")

package ca.warp7.frc.path

import ca.warp7.frc.geometry.*

fun quinticSplineFromPose(p0: Pose2D, p1: Pose2D, bendFactor: Double = 1.2): QuinticSegment2D {
    val scale = p0.translation.distanceTo(p1.translation) * bendFactor
    return QuinticSegment2D(
            x0 = p0.translation.x,
            x1 = p1.translation.x,
            dx0 = p0.rotation.cos * scale,
            dx1 = p1.rotation.cos * scale,
            ddx0 = 0.0,
            ddx1 = 0.0,
            y0 = p0.translation.y,
            y1 = p1.translation.y,
            dy0 = p0.rotation.sin * scale,
            dy1 = p1.rotation.sin * scale,
            ddy0 = 0.0,
            ddy1 = 0.0
    )
}

fun quinticSplinesOf(
        waypoints: List<Pose2D>,
        optimizePath: Boolean = false,
        bendFactor: Double = 1.2
): List<QuinticSegment2D> {
    val path = mutableListOf<QuinticSegment2D>()
    for (i in 0 until waypoints.size - 1) {
        path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1], bendFactor))
    }
    return if (optimizePath) path.optimized() else path
}