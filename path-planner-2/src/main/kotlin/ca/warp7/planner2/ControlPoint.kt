package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D

/**
 * Bridge between user and the segment interface
 */
class ControlPoint(
        val segment: Segment,
        var pose: Pose2D,
        val indexInState: Int,
        val indexInSegment: Int
) {
    var isSelected = false

    override fun equals(other: Any?): Boolean {
        if (other is ControlPoint) return other.pose === pose && other.segment === segment &&
                other.indexInState == indexInState && other.indexInSegment == indexInSegment
        return false
    }

    override fun hashCode(): Int {
        var result = pose.hashCode()
        result = 31 * result + indexInState
        result = 31 * result + indexInSegment
        return result
    }
}