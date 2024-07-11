package org.videolan.moviepedia.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.videolan.moviepedia.database.models.MediaMetadata

@Dao
interface MediaMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mediaMetadata: MediaMetadata): Long

    @Query("SELECT * from media_metadata where ml_id = :id")
    fun getForMedia(id: Long): MediaMetadata
}
