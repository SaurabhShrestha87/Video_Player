package com.video.offline.videoplayer.moviepedia.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.video.offline.videoplayer.moviepedia.database.models.MediaImage

@Dao
interface MediaImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mediaImage: MediaImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mediaImages: List<MediaImage>)

    @Delete
    fun deleteAll(mediaImages: List<MediaImage>)
}
