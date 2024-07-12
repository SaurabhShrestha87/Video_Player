package com.video.offline.videoplayer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.video.offline.videoplayer.mediadb.models.ExternalSub

@Dao
interface ExternalSubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(externalSub: ExternalSub)

    @Query("DELETE FROM external_subtitles_table WHERE idSubtitle = :idSubtitle and mediaPath = :mediaPath")
    fun delete(mediaPath: String, idSubtitle: String)

    @Query("SELECT * from external_subtitles_table where mediaPath = :mediaPath")
    fun get(mediaPath: String): LiveData<List<ExternalSub>>
}
