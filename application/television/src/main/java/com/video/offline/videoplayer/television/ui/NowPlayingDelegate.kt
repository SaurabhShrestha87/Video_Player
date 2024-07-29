package com.video.offline.videoplayer.television.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.videolan.libvlc.MediaPlayer
import com.video.offline.videoplayer.television.viewmodel.MainTvModel
import com.video.offline.videoplayer.PlaybackService
import com.video.offline.videoplayer.media.PlaylistManager
import com.video.offline.videoplayer.util.EmptyPBSCallback

class NowPlayingDelegate(private val model: MainTvModel): PlaybackService.Callback by EmptyPBSCallback {
    private var service: PlaybackService? = null

    private val nowPlayingObserver = Observer<Boolean> { updateCurrent() }

    init {
        PlaylistManager.showAudioPlayer.observeForever(nowPlayingObserver)
        PlaybackService.serviceFlow.onEach { onServiceChanged(it) }
                .onCompletion { service?.removeCallback(this@NowPlayingDelegate) }
                .launchIn(model.viewModelScope)
    }

    fun onClear() {
        PlaylistManager.showAudioPlayer.removeObserver(nowPlayingObserver)
    }

    private fun onServiceChanged(service: PlaybackService?) {
        if (service !== null) {
            this.service = service
            service.addCallback(this)
        } else this.service?.let {
            it.removeCallback(this)
            this.service = null
        }
        updateCurrent()
    }

    override fun onMediaPlayerEvent(event: MediaPlayer.Event) {
        when (event.type) {
            MediaPlayer.Event.Playing -> updateCurrent()
        }
    }

    private fun updateCurrent() = model.run {
        updateNowPlaying()
        if (showHistory) viewModelScope.launch { updateHistory() }
    }
}
