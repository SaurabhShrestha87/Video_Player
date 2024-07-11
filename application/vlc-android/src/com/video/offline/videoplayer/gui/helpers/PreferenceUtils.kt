package com.video.offline.videoplayer.gui.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.video.offline.videoplayer.PlaybackService


suspend fun restartMediaPlayer() = withContext(Dispatchers.Main) {
    val le = PlaybackService.restartPlayer
    if (le.hasObservers()) le.value = true
}