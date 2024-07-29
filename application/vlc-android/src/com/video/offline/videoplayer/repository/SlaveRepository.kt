package com.video.offline.videoplayer.repository

import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.tools.IOScopedObject
import org.videolan.tools.SingletonHolder
import com.video.offline.videoplayer.database.MediaDatabase
import com.video.offline.videoplayer.database.SlaveDao
import com.video.offline.videoplayer.mediadb.models.Slave


class SlaveRepository(private val slaveDao:SlaveDao) : IOScopedObject() {

    fun saveSlave(mediaPath: String, type: Int, priority: Int, uriString: String): Job {
        return launch {
            slaveDao.insert(com.video.offline.videoplayer.mediadb.models.Slave(mediaPath, type, priority, uriString))
        }
    }

    fun saveSlaves(mw: MediaWrapper): List<Job>? {
        return mw.slaves?.let{
            it.map { saveSlave(mw.location, it.type, it.priority, it.uri) }
        }
    }

    suspend fun getSlaves(mrl: String): List<IMedia.Slave> {
        return withContext(Dispatchers.IO) {
            val slaves = try {
                slaveDao.get(mrl)
            } catch (e: SQLiteException) {
                emptyList<com.video.offline.videoplayer.mediadb.models.Slave>()
            }
            val mediaSlaves = slaves.map {
                var uri = it.uri
                if (uri.isNotEmpty())
                    uri = Uri.decode(it.uri)
                IMedia.Slave(it.type, it.priority, uri)
            }
             mediaSlaves
        }
    }

    companion object : SingletonHolder<SlaveRepository, Context>({ SlaveRepository(MediaDatabase.getInstance(it).slaveDao()) })
}
