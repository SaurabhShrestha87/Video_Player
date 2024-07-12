package com.video.offline.videoplayer.database

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.video.offline.videoplayer.mediadb.models.BrowserFav

@Dao
interface BrowserFavDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(browserFav: BrowserFav)

    @Query("SELECT * FROM fav_table where uri = :uri")
    fun get(uri: Uri): List<BrowserFav>

    @Query("SELECT * from fav_table")
    fun getAll(): Flow<List<BrowserFav>>

    @Query("SELECT * from fav_table where type = 0")
    fun getAllNetworkFavs(): Flow<List<BrowserFav>>

    @Query("SELECT * from fav_table where type = 1")
    fun getAllLocalFavs(): LiveData<List<BrowserFav>>

    @Query("DELETE from fav_table where uri = :uri")
    fun delete(uri: Uri)

}