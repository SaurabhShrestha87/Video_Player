package com.video.offline.videoplayer.mediadb

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToUri(value: String): Uri = value.toUri()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}