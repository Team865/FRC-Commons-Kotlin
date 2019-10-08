package ca.warp7.frc.control

/**
 * Helper class for storing and calculating a moving average
 */
@Suppress("MemberVisibilityCanBePrivate")
class MovingAverage(private val maxSize: Int) {
    private val numbers = mutableListOf<Double>()

    val average: Double
        get() {
            var total = 0.0

            for (number in numbers) {
                total += number
            }

            return total / numbers.size
        }

    val size: Int
        get() = numbers.size

    val isUnderMaxSize: Boolean
        get() = size < maxSize

    fun add(newNumber: Double) {
        numbers.add(newNumber)
        if (numbers.size > maxSize) {
            numbers.removeAt(0)
        }
    }

    fun clear() {
        numbers.clear()
    }

}