package com.video.offline.videoplayer.widget.utils

import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.vlc.mediadb.models.Widget

object WidgetCache {
    private val entries = mutableListOf<WidgetCacheEntry>()

    fun getEntry(widget: Widget): WidgetCacheEntry? {
        entries.forEach { if (it.widget.widgetId == widget.widgetId) return it }
        return null
    }

    fun addEntry(widget: Widget): WidgetCacheEntry {
        val widgetCacheEntry = WidgetCacheEntry(widget, currentCoverInvalidated = true)
        entries.add(widgetCacheEntry)
        return widgetCacheEntry
    }

    fun clear(widget: Widget) {
        var entry: WidgetCacheEntry? = null
        entries.forEach { if (it.widget.widgetId == widget.widgetId) entry = it }
        entry?.let {
            entries.remove(it)
        }
    }

}

data class WidgetCacheEntry(val widget: Widget, var currentMedia: MediaWrapper? = null, var currentCover: String? = null, var palette: Palette? = null, @ColorInt var foregroundColor: Int? = null, var playing: Boolean? = null, var currentCoverInvalidated:Boolean = false) {
    fun reset() {
        currentCover = null
        currentMedia = null
    }
}