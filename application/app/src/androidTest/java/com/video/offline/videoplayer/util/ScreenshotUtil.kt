package com.video.offline.videoplayer.util

import tools.fastlane.screengrab.Screengrab

object ScreenshotUtil {

    fun takeScreenshot(number:Int, title:String) {
        Screengrab.screenshot("${normalizeNumber(number)}_$title")
    }

    private fun normalizeNumber(number:Int) = if (number < 10) "0$number" else "$number"
}