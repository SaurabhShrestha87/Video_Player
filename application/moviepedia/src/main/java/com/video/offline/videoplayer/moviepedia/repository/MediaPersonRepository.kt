package com.video.offline.videoplayer.moviepedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import org.videolan.tools.IOScopedObject
import org.videolan.tools.SingletonHolder
import com.video.offline.videoplayer.moviepedia.database.MoviePediaDatabase
import com.video.offline.videoplayer.moviepedia.database.MediaPersonJoinDao
import com.video.offline.videoplayer.moviepedia.database.models.MediaPersonJoin
import com.video.offline.videoplayer.moviepedia.database.models.Person
import com.video.offline.videoplayer.moviepedia.database.models.PersonType

class MediaPersonRepository(private val mediaPersonActorJoinDao: MediaPersonJoinDao) : IOScopedObject() {

    fun addPersons(mediaPersons: List<MediaPersonJoin>) = mediaPersonActorJoinDao.insertPersons(mediaPersons)
    fun removeAllFor(moviepediaId: String) = mediaPersonActorJoinDao.removeAllFor(moviepediaId)

    fun getAll() = mediaPersonActorJoinDao.getAll()

    fun getPersonsByType(moviepediaId: String, personType: PersonType): LiveData<List<Person>> {
        return mediaPersonActorJoinDao.getActorsForMediaLive(moviepediaId, personType)
    }

    companion object : SingletonHolder<MediaPersonRepository, Context>({ MediaPersonRepository(MoviePediaDatabase.getInstance(it).mediaPersonActorJoinDao()) })
}