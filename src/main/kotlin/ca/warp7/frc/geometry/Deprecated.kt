package ca.warp7.frc.geometry

import kotlin.math.atan2
import kotlin.math.hypot


@Deprecated("", ReplaceWith("radians()"))
val Rotation2D.radians: Double get() = atan2(y = sin, x = cos)

@Deprecated("", ReplaceWith("degrees()"))
val Rotation2D.degrees: Double get() = Math.toDegrees(radians())

@Deprecated("", ReplaceWith("mag()"))
val Rotation2D.mag: Double get() = hypot(sin, cos)

@Deprecated("", ReplaceWith("unit()"))
val Rotation2D.norm: Rotation2D get() = scaled(by = 1 / mag())

@Deprecated("", ReplaceWith("translation()"))
val Rotation2D.translation: Translation2D get() = Translation2D(cos, sin)

@Deprecated("", ReplaceWith("normal()"))
val Rotation2D.normal: Rotation2D get() = Rotation2D(-sin, cos)

@Deprecated("", ReplaceWith("transposed()"))
val Translation2D.transposed: Translation2D get() = Translation2D(y, x)

@Deprecated("", ReplaceWith("direction()"))
val Translation2D.direction: Rotation2D get() = Rotation2D(x, y).unit()

@Deprecated("", ReplaceWith("unit()"))
val Translation2D.norm: Translation2D get() = scaled(by = 1 / mag())