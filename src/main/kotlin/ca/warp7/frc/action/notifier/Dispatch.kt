package ca.warp7.frc.action.notifier

import ca.warp7.frc.action.Action

class Dispatch<T : Action>(val action: T){
    fun tryCancel() {

    }
}