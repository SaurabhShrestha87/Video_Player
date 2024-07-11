package com.video.offline.videoplayer.gui.dialogs.adapters


interface VlcTrack {
    fun getName():String
    fun getId():String
    fun getWidth(): Int
    fun getHeight(): Int
    fun getProjection():Int
    fun getFrameRateDen():Int
    fun getFrameRateNum():Int
}