package com.video.offline.videoplayer.repository

import android.content.Context
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.videolan.tools.SingletonHolder
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.database.MediaDatabase
import com.video.offline.videoplayer.database.WidgetDao
import org.videolan.vlc.mediadb.models.Widget
import com.video.offline.videoplayer.widget.utils.WidgetCache


class WidgetRepository(private val widgetDao: WidgetDao) {

    suspend fun getAllWidgets() = withContext(Dispatchers.IO) {
        widgetDao.getAll()
    }

    suspend fun getWidget(id: Int) = withContext(Dispatchers.IO) {
        widgetDao.get(id)
    }

    fun getWidgetFlow(id: Int): Flow<Widget> {
        return widgetDao.getFlow(id)
    }

    suspend fun addWidget(widget: Widget) = withContext(Dispatchers.IO) {
        widgetDao.insert(widget)
    }

    suspend fun updateWidget(widget: Widget, preventCacheClear:Boolean = false) {
        if (!preventCacheClear) WidgetCache.clear(widget)
        withContext(Dispatchers.IO) {
            widgetDao.update(widget)
        }
    }

    suspend fun deleteWidget(id: Int) = withContext(Dispatchers.IO) {
        widgetDao.delete(id)
    }

    suspend fun createNew(context: Context, appWidgetId: Int): Widget {
        val widget = Widget(appWidgetId, 0, 0, 0, 0, true, ContextCompat.getColor(context, R.color.black), ContextCompat.getColor(context, R.color.white), 10, 10, 100, showConfigure = true, showSeek = true, showCover = true)
        addWidget(widget)
        return widget
    }


    companion object : SingletonHolder<WidgetRepository, Context>({ WidgetRepository(MediaDatabase.getInstance(it).widgetDao()) })
}
