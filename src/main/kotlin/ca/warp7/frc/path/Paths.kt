@file:Suppress("unused")

package ca.warp7.frc.path

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*

operator fun Path2D.get(t: Double): Path2DState {
    return Path2DState(t, px(t), py(t), vx(t), vy(t), ax(t), ay(t), jx(t), jy(t))
}

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
        vararg waypoints: Pose2D,
        optimizePath: Boolean = false,
        bendFactor: Double = 1.2
): List<QuinticSegment2D> {
    val path = mutableListOf<QuinticSegment2D>()
    for (i in 0 until waypoints.size - 1) {
        path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1], bendFactor))
    }
    return if (optimizePath) path.optimized() else path
}

fun parameterizedSplinesOf(vararg waypoints: Pose2D): List<ArcPose2D> =
        quinticSplinesOf(*waypoints).parameterized()

private fun mixParameterizeSplines(
        path: MutableList<QuinticSegment2D>,
        param: MutableList<ArcPose2D>,
        optimizePath: Boolean
) {
    val segment = if (optimizePath) path.optimized().parameterized() else path.parameterized()
    if (param.isEmpty()) param.addAll(segment)
    else param.addAll(segment.subList(0, segment.lastIndex))
}

fun mixParameterizedPathOf(
        waypoints: Array<Pose2D>,
        optimizePath: Boolean,
        bendFactor: Double
): List<ArcPose2D> {
    val param = mutableListOf<ArcPose2D>()
    val path = mutableListOf<QuinticSegment2D>()
    for (i in 0 until waypoints.size - 1) {
        val a = waypoints[i]
        val b = waypoints[i + 1]
        if (a.translation.epsilonEquals(b.translation)) {
            if (path.isNotEmpty()) {
                mixParameterizeSplines(path, param, optimizePath)
                path.clear()
            }
            val theta = (b.rotation - a.rotation).radians
            val phi = a.rotation.radians
            check(!theta.epsilonEquals(0.0))
            var x = 0.0
            if (theta > 0) {
                while (x < theta) {
                    x += 0.1
                    param.add(ArcPose2D(Pose2D(a.translation,
                            Rotation2D.fromRadians(phi + x)), Double.POSITIVE_INFINITY, 0.0))
                }
            } else {
                while (x > theta) {
                    x -= 0.1
                    param.add(ArcPose2D(Pose2D(a.translation,
                            Rotation2D.fromRadians(phi + x)), Double.NEGATIVE_INFINITY, 0.0))
                }
            }
        } else {
            path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1], bendFactor))
        }
    }
    if (path.isNotEmpty()) {
        mixParameterizeSplines(path, param, optimizePath)
    }
    return param
}