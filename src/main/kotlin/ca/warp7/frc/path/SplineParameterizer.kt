package ca.warp7.frc.path

import ca.warp7.frc.geometry.ArcPose2D

fun List<QuinticSegment2D>.parameterized(): List<ArcPose2D> {
    val points = mutableListOf<ArcPose2D>()
    val p0 = first()[0.0]
    points.add(ArcPose2D(p0.pose(), p0.curvature(), 0.0))
    forEach { points.addAll(it.parameterized()) }
    return points
}

fun QuinticSegment2D.parameterized(): List<ArcPose2D> {
    val points = mutableListOf<ArcPose2D>()
    parameterize(points, 0.0, 1.0)
    return points
}

fun QuinticSegment2D.parameterize(
        points: MutableList<ArcPose2D>,
        t0: Double,
        t1: Double
) {
    val p0 = get(t0)
    val p1 = get(t1)
    val pose1 = p1.pose()
    // get the twist transformation between start and and points
    val twist = (pose1 - p0.pose()).log()
    // check if the twist is within threshold
    if (twist.dx > 0.127 || twist.dy > 0.00127 || twist.dTheta > 0.0872) {
        // partition and re-parameterize
        parameterize(points, t0, (t0 + t1) / 2.0)
        parameterize(points, (t0 + t1) / 2.0, t1)
    } else {
        points.add(ArcPose2D(pose1, p1.curvature(), 0.0))
    }
}