package com.video.offline.videoplayer.moviepedia.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.video.offline.videoplayer.moviepedia.database.models.MediaImageType
import com.video.offline.videoplayer.moviepedia.database.models.MediaMetadataType
import com.video.offline.videoplayer.moviepedia.database.models.PersonType
import java.util.*

class Converters {
    @TypeConverter fun uriToString(uri: Uri): String = uri.toString()
    @TypeConverter fun stringToUri(value: String): Uri = value.toUri()
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    //Person type
    @TypeConverter
    fun personTypeToKey(personType: PersonType): Int {
        return personType.key
    }

    @TypeConverter
    fun personTypeFromKey(key: Int): PersonType {
        return PersonType.fromKey(key)
    }

    //Media image type
    @TypeConverter
    fun mediaImageTypeToKey(mediaImageType: MediaImageType): Int {
        return mediaImageType.key
    }

    @TypeConverter
    fun mediaImageTypeFromKey(key: Int): MediaImageType {
        return MediaImageType.fromKey(key)
    }

    //Media type
    @TypeConverter
    fun mediaTypeToKey(mediaType: MediaMetadataType): Int {
        return mediaType.key
    }

    @TypeConverter
    fun mediaTypeFromKey(key: Int): MediaMetadataType {
        return MediaMetadataType.fromKey(key)
    }
}