package com.video.offline.videoplayer.mediadb.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_table")
data class BrowserFav(
        @PrimaryKey
        @ColumnInfo(name = "uri")
        val uri: Uri,
        @ColumnInfo(name = "type")
        val type: Int,
        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "icon_url")
        val iconUrl: String?
)
