package com.video.offline.videoplayer.database

import androidx.room.*
import com.video.offline.videoplayer.mediadb.models.CustomDirectory

@Dao
interface CustomDirectoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(customDirectory: CustomDirectory)

    @Delete
    fun delete(customDirectory: CustomDirectory)

    @Query("SELECT * FROM CustomDirectory")
    fun getAll(): List<CustomDirectory>

    @Query("SELECT * FROM CustomDirectory WHERE path = :path")
    fun get(path: String): List<CustomDirectory>
}
