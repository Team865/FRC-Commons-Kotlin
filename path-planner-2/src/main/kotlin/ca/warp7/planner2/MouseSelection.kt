package ca.warp7.planner2

import ca.warp7.frc.geometry.Pose2D

class MouseSelection(
        val segment: Segment,
        val segmentIndex: Int,
        val point: Pose2D,
        val pointIndex: Int
)