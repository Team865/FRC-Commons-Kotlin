package ca.warp7.pathplanner

// Unit conversion constants

const val kFeetToMetres: Double = 0.3048
const val kInchesToMetres: Double = 0.0254
const val kFeetToInches: Double = 12.0

// Dimension Constants

const val kWheelRadius = 2.95 * kInchesToMetres // m
const val kTurningDiameter = 24.75 * kInchesToMetres // m

// Kinematic constants

const val kMaxVelocity = 12.0 * kFeetToMetres // m/s
const val kMaxAcceleration = 9.0 * kFeetToMetres //  m/s^2
const val kMaxFreeSpeed = 16.5 * kFeetToMetres// m/s

const val kScrubFactor = 1.45
const val kEffectiveWheelBaseRadius = kTurningDiameter / 2 * kScrubFactor // m

// Dynamic constants

const val kMaxVolts = 12.0 // V
const val kFrictionVoltage = 1.0 // V
const val kLinearInertia = 60.0 // kg
const val kAngularInertia = 10.0 // kg * m^2
const val kAngularDrag = 20.0 // (N * m) / (rad/s)
const val kSpeedPerVolt = (kMaxFreeSpeed / kWheelRadius) / (kMaxVolts - kFrictionVoltage) // (rad/s) / V
const val kA = 80.0 // // (rad/s^2) / V
const val kTorquePerVolt = 0.5 * kWheelRadius * kWheelRadius * kLinearInertia * kA  // (N * m) / V