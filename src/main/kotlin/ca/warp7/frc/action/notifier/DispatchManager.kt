package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action

class DispatchManager {
    operator fun contains(action: Action): Boolean {
        return true
    }

     fun manages(action: Action): Boolean {
        return true
    }
}