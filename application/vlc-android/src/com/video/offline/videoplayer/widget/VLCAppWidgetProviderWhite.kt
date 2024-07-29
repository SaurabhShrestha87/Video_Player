package com.video.offline.videoplayer.widget

import com.video.offline.videoplayer.R

class VLCAppWidgetProviderWhite : VLCAppWidgetProvider() {


    override fun getlayout(): Int {
        return R.layout.widget_old
    }


    override fun getPlayPauseImage(isPlaying: Boolean): Int {
        return if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
    }

}
