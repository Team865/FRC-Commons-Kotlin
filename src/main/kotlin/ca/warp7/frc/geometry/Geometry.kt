@file:JvmName("Geometry")

package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import kotlin.math.*

fun Pose2D.isColinear(other: Pose2D): Boolean {
    if (!rotation.parallelTo(other.rotation)) return false
    val twist = (other - this).log()
    return twist.dy.epsilonEquals(0.0) && twist.dTheta.epsilonEquals(0.0)
}

fun Pose2D.intersection(other: Pose2D): Translation2D {
    val otherRotation = other.rotation
    if (rotation.parallelTo(otherRotation)) {
        // Lines are parallel.
        return Translation2D(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY)
    }
    return if (abs(rotation.cos) < abs(otherRotation.cos)) {
        intersectionInternal(this, other)
    } else {
        intersectionInternal(other, this)
    }
}

private fun intersectionInternal(a: Pose2D, b: Pose2D): Translation2D {
    val ar = a.rotation
    val br = b.rotation
    val at = a.translation
    val bt = b.translation

    val tanB = br.tan()
    val t = ((at.x - bt.x) * tanB + bt.y - at.y) / (ar.sin - ar.cos * tanB)
    return if (t.isNaN()) {
        Translation2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    } else at + ar.translation() * t
}

fun getDirection(pose: Pose2D, point: Pose2D): Double {
    val poseToPoint = point.translation - pose.translation
    val robot = pose.rotation.translation()
    return if (robot cross poseToPoint < 0.0) -1.0 else 1.0 // if robot < pose turn left
}

fun findCenter(pose: Pose2D, point: Pose2D): Translation2D {
    val poseToPointHalfway = pose.translation.interpolate(point.translation, 0.5)
    val normal = (pose.translation.inverse + poseToPointHalfway).direction().normal()
    val perpendicularBisector = Pose2D(poseToPointHalfway, normal)
    val normalFromPose = Pose2D(pose.translation, pose.rotation.normal())
    return if (normalFromPose.isColinear(perpendicularBisector.run { Pose2D(translation, rotation.normal()) })) {
        // Special case: center is poseToPointHalfway.
        poseToPointHalfway
    } else normalFromPose.intersection(perpendicularBisector)
}

fun findRadius(pose: Pose2D, point: Pose2D): Double {
    return (point.translation - findCenter(pose, point)).mag() * getDirection(pose, point)
}