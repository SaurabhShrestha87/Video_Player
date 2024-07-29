package com.video.offline.videoplayer.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.videolan.tools.CoroutineContextProvider
import org.videolan.tools.SingletonHolder
import org.videolan.tools.livedata.LiveDataMap
import com.video.offline.videoplayer.database.ExternalSubDao
import com.video.offline.videoplayer.database.MediaDatabase
import com.video.offline.videoplayer.gui.dialogs.State
import com.video.offline.videoplayer.gui.dialogs.SubtitleItem
import java.io.File

class ExternalSubRepository(private val externalSubDao: ExternalSubDao, private val coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider()) {

    private var _downloadingSubtitles = LiveDataMap<Long, SubtitleItem>()

    @Suppress("UNCHECKED_CAST")
    val downloadingSubtitles: LiveData<Map<Long, SubtitleItem>>
        get() = _downloadingSubtitles as LiveData<Map<Long, SubtitleItem>>

    fun saveDownloadedSubtitle(idSubtitle: String, subtitlePath: String, mediaPath: String, language: String, movieReleaseName: String): Job {
        return GlobalScope.launch(coroutineContextProvider.IO) { externalSubDao.insert(com.video.offline.videoplayer.mediadb.models.ExternalSub(idSubtitle, subtitlePath, mediaPath, language, movieReleaseName)) }
    }

    fun getDownloadedSubtitles(mediaUri: Uri): LiveData<List<com.video.offline.videoplayer.mediadb.models.ExternalSub>> {
        val externalSubs = externalSubDao.get(mediaUri.path!!)
        return externalSubs.map { list ->
            val existExternalSubs: MutableList<com.video.offline.videoplayer.mediadb.models.ExternalSub> = mutableListOf()
            list.forEach {
                if (File(Uri.decode(it.subtitlePath)).exists())
                    existExternalSubs.add(it)
                else
                    deleteSubtitle(it.mediaPath, it.idSubtitle)
            }
            existExternalSubs
        }
    }

    fun deleteSubtitle(mediaPath: String, idSubtitle: String) {
        GlobalScope.launch { externalSubDao.delete(mediaPath, idSubtitle) }
    }

    fun addDownloadingItem(key: Long, item: SubtitleItem) {
        _downloadingSubtitles.add(key, item.copy(state = State.Downloading))
    }

    fun removeDownloadingItem(key: Long) {
        _downloadingSubtitles.remove(key)
    }

    fun getDownloadingSubtitle(key: Long) = _downloadingSubtitles.get(key)

    companion object : SingletonHolder<ExternalSubRepository, Context>({ ExternalSubRepository(MediaDatabase.getInstance(it).externalSubDao()) })
}