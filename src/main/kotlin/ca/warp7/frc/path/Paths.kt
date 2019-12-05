@file:Suppress("unused")

package ca.warp7.frc.path

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

fun parameterizeQuickTurn(a: Rotation2D, b: Rotation2D): List<ArcPose2D> {
    val startingAngle = a.radians()
    val theta = (b - a).radians()
    require(theta != 0.0) {
        "QuickTurn Generator - Two points are the same"
    }
    val quickTurnAngles = mutableListOf<Rotation2D>()
    var x = 0.0
    return if (theta > 0) {
        while (x < theta) {
            x += 0.1
            quickTurnAngles.add(Rotation2D.fromRadians(startingAngle + x))
        }
        quickTurnAngles.map { ArcPose2D(Pose2D(a.translation(), it), Double.POSITIVE_INFINITY, 0.0) }
    } else {
        while (x > theta) {
            x -= 0.1
            quickTurnAngles.add(Rotation2D.fromRadians(startingAngle + x))
        }
        quickTurnAngles.map { ArcPose2D(Pose2D(a.translation(), it), Double.NEGATIVE_INFINITY, 0.0) }
    }
}