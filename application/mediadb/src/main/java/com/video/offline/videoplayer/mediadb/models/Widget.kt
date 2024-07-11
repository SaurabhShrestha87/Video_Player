package org.videolan.vlc.mediadb.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_table")
data class Widget(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val widgetId: Int,
        @ColumnInfo(name = "width")
        var width: Int,
        @ColumnInfo(name = "height")
        var height: Int,
        @ColumnInfo(name = "theme")
        var theme: Int,
        @ColumnInfo(name = "type")
        var type: Int,
        @ColumnInfo(name = "light_theme")
        var lightTheme: Boolean,
        @ColumnInfo(name = "background_color")
        var backgroundColor: Int,
        @ColumnInfo(name = "foreground_color")
        var foregroundColor: Int,
        @ColumnInfo(name = "forward_delay")
        var forwardDelay: Int,
        @ColumnInfo(name = "rewind_delay")
        var rewindDelay: Int,
        @ColumnInfo(name = "opacity")
        var opacity: Int,
        @ColumnInfo(name = "show_configure")
        var showConfigure: Boolean,
        @ColumnInfo(name = "show_seek")
        var showSeek: Boolean,
        @ColumnInfo(name = "show_cover")
        var showCover: Boolean
)
