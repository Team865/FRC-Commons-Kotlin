package test.ca.warp7.frc.path

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc.path.mixParameterizedPathOf

fun main() {
    val a = mixParameterizedPathOf(arrayOf(
            Pose2D.identity,
            Pose2D(Translation2D(2.0, 2.0), Rotation2D.identity),
            Pose2D(Translation2D(2.0, 2.0), Rotation2D.fromDegrees(90.0))
    ), true, 1.2)
    println(a.joinToString("\n"))
}