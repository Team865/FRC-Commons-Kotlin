package ca.warp7.frc.action.notifier

import java.util.ArrayList
import java.util.concurrent.locks.ReentrantLock

internal val managersMutex = ReentrantLock()
internal val managers: MutableList<DispatchManager> = ArrayList()