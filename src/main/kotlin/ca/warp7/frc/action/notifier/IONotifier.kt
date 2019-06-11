package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.IOAction
import edu.wpi.first.wpilibj.Notifier

class IONotifier(
        val io: IOAction
): Notifier(null) {

    init {
        setHandler {
            io.readInputs()
            io.writeOutputs()
        }
    }

    fun cancelAll() {

    }
}