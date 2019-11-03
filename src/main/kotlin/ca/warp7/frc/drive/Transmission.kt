package ca.warp7.frc.drive

import kotlin.math.max
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate")
class Transmission(
        val speedPerVolt: Double, // (rad/s) / V
        val torquePerVolt: Double, // (N * m) / V
        val frictionVoltage: Double, // V
        val maxVoltage: Double // V
) {

    companion object {
        const val kEpsilon = 1E-9
    }

    /**
     * Get the motor torque for a specified speed and voltage
     *
     * calculate the effective voltage (taking away the friction voltage),
     * then calculate torque based on the voltage left
     * Units: ((N * m) / V) * (V - (rad/s) / ((rad/s) / V) = N * m
     *
     * @param speed speed in rad/s
     * @param voltage voltage in V
     * @return torque in N * m
     */
    fun torqueForVoltage(speed: Double, voltage: Double): Double {

        var effectiveVoltage = voltage
        when {
            speed > kEpsilon -> effectiveVoltage -= frictionVoltage
            speed < -kEpsilon -> effectiveVoltage += frictionVoltage
            voltage > kEpsilon -> effectiveVoltage = max(0.0, voltage - frictionVoltage)
            voltage < -kEpsilon -> effectiveVoltage = min(0.0, voltage + frictionVoltage)
            else -> return 0.0
        }

        return torquePerVolt * (effectiveVoltage - speed / speedPerVolt)
    }


    /**
     * Get the motor voltage for a specified speed and torque
     *
     * find out the sign of the friction voltage that needs to be applied,
     * then convert to volts and add the signed friction voltage
     * Units: (N * m) / ((N * m) / V) + (rad/s) / ((rad/s) / V) + V = V
     *
     * @param chassisSpeed the speed of the chassis to determine friction voltage
     * @param wheelSpeed the desired speed in rad/s
     * @param wheelTorque the desired torque in N * m
     * @return voltage in V
     */
    fun voltageForTorque(chassisSpeed: Double, wheelSpeed: Double, wheelTorque: Double): Double {
        val frictionVoltage = when {
            chassisSpeed > kEpsilon -> frictionVoltage
            chassisSpeed < -kEpsilon -> -frictionVoltage
            wheelSpeed > kEpsilon -> frictionVoltage
            wheelSpeed < -kEpsilon -> -frictionVoltage
            wheelTorque > kEpsilon -> frictionVoltage
            wheelTorque < -kEpsilon -> -frictionVoltage
            else -> return 0.0
        }
        return wheelTorque / torquePerVolt + wheelSpeed / speedPerVolt + frictionVoltage
    }
}