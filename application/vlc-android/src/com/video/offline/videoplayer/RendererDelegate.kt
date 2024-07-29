package com.video.offline.videoplayer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.videolan.libvlc.RendererDiscoverer
import org.videolan.libvlc.RendererItem
import org.videolan.resources.AppContextProvider
import org.videolan.resources.VLCInstance
import org.videolan.tools.AppScope
import org.videolan.tools.NetworkMonitor
import org.videolan.tools.isAppStarted
import org.videolan.tools.livedata.LiveDataset
import org.videolan.tools.retry
import java.util.*

object RendererDelegate : RendererDiscoverer.EventListener {

    private const val TAG = "VLC/RendererDelegate"
    private val discoverers = ArrayList<RendererDiscoverer>()
    val renderers : LiveDataset<RendererItem> = LiveDataset()

    @Volatile private var started = false

    init {
        NetworkMonitor.getInstance(AppContextProvider.appContext).connectionFlow.onEach { if (it.connected) start() else stop() }.launchIn(AppScope)
    }

    suspend fun start() {
        if (started) return
        val libVlc = withContext(Dispatchers.IO) { VLCInstance.getInstance(AppContextProvider.appContext) }
        started = true
        for (discoverer in RendererDiscoverer.list(libVlc)) {
            val rd = RendererDiscoverer(libVlc, discoverer.name)
            discoverers.add(rd)
            rd.setEventListener(this@RendererDelegate)
            retry(5, 1000L) { if (!rd.isReleased) rd.start() else false }
        }
    }

    fun stop() {
        if (!started) return
        started = false
        for (discoverer in discoverers) discoverer.stop()
        if (isAppStarted() || PlaybackService.instance?.run { !isPlaying } != false) {
            PlaybackService.renderer.value = null
        }
        clear()
    }

    private fun clear() {
        discoverers.clear()
        renderers.clear()
    }

    override fun onEvent(event: RendererDiscoverer.Event?) {
        when (event?.type) {
            RendererDiscoverer.Event.ItemAdded -> renderers.add(event.item)
            RendererDiscoverer.Event.ItemDeleted -> renderers.remove(event.item)
        }
    }
}
