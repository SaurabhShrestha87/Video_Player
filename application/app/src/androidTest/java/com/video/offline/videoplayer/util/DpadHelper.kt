package com.video.offline.videoplayer.util

import android.os.SystemClock
import android.view.KeyEvent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice

object DpadHelper {
    private val device: UiDevice by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    fun pressDPad(direction: Direction?, nbTimes: Int = 1) {
        for (i in 0 until nbTimes) {
            when (direction) {
                Direction.DOWN -> device.pressDPadDown()
                Direction.LEFT -> device.pressDPadLeft()
                Direction.UP -> device.pressDPadUp()
                Direction.RIGHT -> device.pressDPadRight()
                else -> {}
            }
            if (i < nbTimes - 1) SystemClock.sleep(300)
        }
    }

    fun pressHome() = device.pressHome()
    fun pressPip() = device.pressKeyCode(KeyEvent.KEYCODE_WINDOW)
    fun pressStop() = device.pressKeyCode(KeyEvent.KEYCODE_MEDIA_STOP)
    fun pressBack() = device.pressBack()
    fun pressDPadCenter() = device.pressDPadCenter()
}