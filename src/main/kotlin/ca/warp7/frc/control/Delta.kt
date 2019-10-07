package ca.warp7.frc.control

class Delta {
    var value = 0.0

    fun update(newValue: Double): Double {
        val oldValue = value
        value = newValue
        return newValue - oldValue
    }
}