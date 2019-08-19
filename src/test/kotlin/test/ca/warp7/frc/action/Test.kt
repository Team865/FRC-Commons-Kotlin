@file:Suppress("unused")

package test.ca.warp7.frc.action

import ca.warp7.frc.action.*

fun test(): Action {
    return action {
        onStart { }
        onUpdate { }
        onStop {
        }
        finishWhen { true }
    }
}

fun test2(): Action {
    return queue {
        +runOnce {
        }

        +periodic {
        }

        +action {
            printTaskGraph()
            onStart { }
            finishWhen { true }
            onUpdate { }
            onStop { }
        }

        +wait(1)
        +waitUntil { true }

        +async {
            +runOnce {

            }
        }
    }
}

fun test3(): Action {
    return async {
    }
}