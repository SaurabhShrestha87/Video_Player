package com.video.offline.videoplayer.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.videolan.vlc.mediadb.models.Widget

@Dao
interface WidgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(widget: Widget)

    @Update
    fun update(widget: Widget)

    @Query("SELECT * FROM widget_table where id = :widgetId")
    fun get(widgetId: Int): Widget?


    @Query("SELECT * FROM widget_table where id = :widgetId")
    fun getFlow(widgetId: Int): Flow<Widget>

    @Query("SELECT * FROM widget_table")
    fun getAll(): List<Widget>

    @Query("DELETE from widget_table where id = :id")
    fun delete(id: Int)

}