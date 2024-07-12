package com.video.offline.videoplayer.moviepedia.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.video.offline.videoplayer.moviepedia.database.MoviePediaDatabase
import org.videolan.tools.IOScopedObject
import org.videolan.tools.SingletonHolder
import com.video.offline.videoplayer.moviepedia.database.MediaImageDao
import com.video.offline.videoplayer.moviepedia.database.MediaMetadataDao
import com.video.offline.videoplayer.moviepedia.database.MediaMetadataDataFullDao
import com.video.offline.videoplayer.moviepedia.database.models.MediaImage
import com.video.offline.videoplayer.moviepedia.database.models.MediaMetadata
import com.video.offline.videoplayer.moviepedia.database.models.MediaMetadataType
import com.video.offline.videoplayer.moviepedia.database.models.MediaMetadataWithImages

class MediaMetadataRepository(private val mediaMetadataFullDao: MediaMetadataDataFullDao, private val mediaMetadataDao: MediaMetadataDao, private val mediaImageDao: MediaImageDao) : IOScopedObject() {

    @WorkerThread
    fun addMetadataImmediate(mediaMetadata: MediaMetadata) = mediaMetadataDao.insert(mediaMetadata)

    @WorkerThread
    fun addImagesImmediate(images: List<MediaImage>) = mediaImageDao.insertAll(images)

    @WorkerThread
    fun deleteImages(images: List<MediaImage>) = mediaImageDao.deleteAll(images)

    @WorkerThread
    fun getMetadataLiveByML(mediaId: Long): LiveData<MediaMetadataWithImages?> = mediaMetadataFullDao.getMetadataLiveByML(mediaId)

    @WorkerThread
    fun findNextEpisode(showId: String, season: Int, episode: Int): MediaMetadataWithImages? = mediaMetadataFullDao.findNextEpisode(showId, season, episode)

    @WorkerThread
    fun getMetadataLive(mediaId: String): LiveData<MediaMetadataWithImages?> = mediaMetadataFullDao.getMediaLive(mediaId)

    @WorkerThread
    fun getEpisodesLive(showId: String): LiveData<List<MediaMetadataWithImages>> = mediaMetadataFullDao.getEpisodesLive(showId)

    @WorkerThread
    fun getMovieCount(): Int = mediaMetadataFullDao.getMovieCount()

    @WorkerThread
    fun getTvshowsCount(): Int = mediaMetadataFullDao.getTvshowsCount()

    @WorkerThread
    fun getMetadata(mediaId: Long): MediaMetadataWithImages? = mediaMetadataFullDao.getMedia(mediaId)

    @WorkerThread
    fun getMediaById(mediaId: String): MediaMetadataWithImages? = mediaMetadataFullDao.getMediaById(mediaId)

    fun getMoviePagedList(sortField: String, sortType: String, metadataType: MediaMetadataType): DataSource.Factory<Int, MediaMetadataWithImages> {
        val query = SimpleSQLiteQuery("SELECT * FROM media_metadata WHERE type = ${metadataType.key} ORDER BY $sortField $sortType")
        return mediaMetadataFullDao.getAllPaged(query)
    }

    fun getAllLive(): LiveData<List<MediaMetadataWithImages>> = mediaMetadataFullDao.getAllLive()

    fun getTvshow(showId: String) = mediaMetadataFullDao.getMediaById(showId)

    fun getTvshowLive(showId: String) = mediaMetadataFullDao.getMediaByIdLive(showId)

    fun getByIds(mlids: List<Long>) = mediaMetadataFullDao.getByIds(mlids)

    fun getRecentlyAdded() = mediaMetadataFullDao.getRecentlyAdded()

    fun searchMedia(sanitizedQuery: String) = mediaMetadataFullDao.searchMedia(sanitizedQuery)

    fun getTvShowEpisodes(tvshowId: String) = mediaMetadataFullDao.getTvShowEpisodes(tvshowId)

    companion object : SingletonHolder<MediaMetadataRepository, Context>({ MediaMetadataRepository(MoviePediaDatabase.getInstance(it).mediaMedataDataFullDao(), MoviePediaDatabase.getInstance(it).mediaMetadataDao(), MoviePediaDatabase.getInstance(it).mediaImageDao()) })
}
