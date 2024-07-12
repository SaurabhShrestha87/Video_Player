package com.video.offline.videoplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.video.offline.videoplayer.mediadb.models.Slave

@Dao
interface SlaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(slave: Slave)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(slaves: Array<Slave>)

    @Query("SELECT * from SLAVES_table where slave_media_mrl = :mrl")
    fun get(mrl: String): List<Slave>
}