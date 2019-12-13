package ca.warp7.frc.path

import ca.warp7.frc.geometry.ArcPose2D
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import kotlin.math.hypot

fun List<QuinticSegment2D>.parameterized(): List<ArcPose2D> {
    val points = mutableListOf<ArcPose2D>()
    val p0 = first()[0.0]
    points.add(ArcPose2D(p0.pose(), p0.curvature()))
    forEach { points.addAll(it.parameterized()) }
    return points
}

fun QuinticSegment2D.parameterized(): List<ArcPose2D> {
    val points = mutableListOf<ArcPose2D>()
    parameterize(points, 0.0, 1.0)
    return points
}

fun QuinticSegment2D.getState(t: Double): Path2DState {
    return Path2DState(0.0, px(t), py(t), vx(t), vy(t), ax(t), ay(t), 0.0, 0.0)
}

fun QuinticSegment2D.getPose(t: Double): Pose2D {
    val vx = vx(t)
    val vy = vy(t)
    val hp = hypot(vx, vy)
    return Pose2D(Translation2D(px(t), py(t)), Rotation2D(vx/hp, vy/hp))
}

fun QuinticSegment2D.parameterize(
        points: MutableList<ArcPose2D>,
        t0: Double,
        t1: Double
) {
    val state = getState(t1)
    val statePose = state.pose()
    val twist = (statePose - getPose(t0)).log()
    if (twist.dx > 0.127 || twist.dy > 0.00127 || twist.dTheta > 0.0872) {
        parameterize(points, t0, (t0 + t1) / 2.0)
        parameterize(points, (t0 + t1) / 2.0, t1)
    } else {
        points.add(ArcPose2D(statePose, state.curvature()))
    }
}