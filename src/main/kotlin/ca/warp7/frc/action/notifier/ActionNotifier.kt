package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action

interface ActionNotifier {
    fun cancelAll()
    fun setAction(actionDSL: Action)
}