package org.videolan.moviepedia.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import org.videolan.moviepedia.database.models.MediaMetadataWithImages

@Dao
interface MediaMetadataDataFullDao {

    @Query("select * from media_metadata where ml_id = :id")
    fun getMetadataLiveByML(id: Long): LiveData<MediaMetadataWithImages?>

    @Query("select * from media_metadata where moviepedia_id = :id")
    fun getMediaLive(id: String): LiveData<MediaMetadataWithImages?>

    @Query("select * from media_metadata where show_id = :showId")
    fun getEpisodesLive(showId: String): LiveData<List<MediaMetadataWithImages>>

    @Query("select * from media_metadata where ml_id = :id")
    fun getMedia(id: Long): MediaMetadataWithImages?

    @Query("select * from media_metadata where moviepedia_id = :id")
    fun getMediaById(id: String): MediaMetadataWithImages?

    @Query("select * from media_metadata where moviepedia_id = :id")
    fun getMediaByIdLive(id: String): LiveData<MediaMetadataWithImages>

    @Query("select count(moviepedia_id) from media_metadata where type = 0")
    fun getMovieCount(): Int

    @Query("select count(moviepedia_id) from media_metadata where type = 2")
    fun getTvshowsCount(): Int

    @RawQuery(observedEntities = [MediaMetadataWithImages::class])
    fun getAllPaged(query: SupportSQLiteQuery): DataSource.Factory<Int, MediaMetadataWithImages>

    @Query("select * from media_metadata")
    fun getAllLive(): LiveData<List<MediaMetadataWithImages>>

    @Query("SELECT * FROM media_metadata WHERE show_id = :showId AND ((season = :season AND episode > :episode) OR (season > :season)) ORDER BY season, episode ASC")
    fun findNextEpisode(showId: String, season: Int, episode: Int): MediaMetadataWithImages?

    @Query("select * from media_metadata where ml_id IN (:mlids) LIMIT 10")
    fun getByIds(mlids: List<Long>): LiveData<List<MediaMetadataWithImages>>

    @Query("select * from media_metadata ORDER BY insertDate DESC LIMIT 10")
    fun getRecentlyAdded(): LiveData<List<MediaMetadataWithImages>>

    @Query("select * from media_metadata WHERE title LIKE :sanitizedQuery")
    fun searchMedia(sanitizedQuery: String): List<MediaMetadataWithImages>

    @Query("select * from media_metadata WHERE show_id = :tvshowId ORDER by season, episode")
    fun getTvShowEpisodes(tvshowId: String): List<MediaMetadataWithImages>

}