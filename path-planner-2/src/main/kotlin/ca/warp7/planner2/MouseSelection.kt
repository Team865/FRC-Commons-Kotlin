package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D

class MouseSelection(
        val segment: Segment,
        val segmentIndex: Int,
        val point: Pose2D,
        val pointIndex: Int
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MouseSelection) return false
        if (segmentIndex != other.segmentIndex) return false
        if (pointIndex != other.pointIndex) return false
        return true
    }

    override fun hashCode(): Int {
        var result = segmentIndex
        result = 31 * result + pointIndex
        return result
    }
}