package com.video.offline.videoplayer.mediadb.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "SLAVES_table", primaryKeys = ["slave_media_mrl", "slave_uri"])
data class Slave (
    @ColumnInfo(name = "slave_media_mrl")
    val mediaPath: String,
    @ColumnInfo(name = "slave_type")
    val type: Int,
    @ColumnInfo(name = "slave_priority")
    val priority:Int,
    @ColumnInfo(name = "slave_uri")
    val uri: String
)

