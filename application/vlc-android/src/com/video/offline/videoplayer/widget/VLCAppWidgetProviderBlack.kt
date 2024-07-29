package com.video.offline.videoplayer.widget

import com.video.offline.videoplayer.R

class VLCAppWidgetProviderBlack : VLCAppWidgetProvider() {

    override fun getlayout(): Int = R.layout.widget_old

    override fun getPlayPauseImage(isPlaying: Boolean): Int {
        return if (isPlaying) R.drawable.ic_widget_pause_w else R.drawable.ic_widget_play_w
    }

}
