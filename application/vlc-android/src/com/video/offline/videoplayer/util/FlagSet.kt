package com.video.offline.videoplayer.util

import java.util.EnumSet

class FlagSet<T>(private val enumClass: Class<T>) where T : Enum<T>, T : Flag {
    private val enabledActions = EnumSet.noneOf(enumClass)

    override fun toString() = enabledActions.toString()

    fun add(action: T) = enabledActions.add(action)

    fun remove(action: T) = enabledActions.remove(action)

    fun addAll(vararg actions: T) = actions.forEach { add(it) }

    fun removeAll(vararg actions: T) = actions.forEach { remove(it) }

    fun contains(action: T) = enabledActions.contains(action)

    fun isNotEmpty() = enabledActions.isNotEmpty()

    fun getCapabilities(): Long = enabledActions.fold(0L) { capabilities, action -> capabilities or action.toLong() }

    fun setCapabilities(capabilities: Long) {
        if (capabilities == 0L) return

        var remainingBits = capabilities
        for (action in enumClass.enumConstants) {
            val element = action.toLong()
            if (capabilities and element != 0L) {
                enabledActions.add(action)
                remainingBits = remainingBits xor element
                if (remainingBits == 0L) break
            }
        }
    }
}

interface Flag {
    fun toLong(): Long
}
