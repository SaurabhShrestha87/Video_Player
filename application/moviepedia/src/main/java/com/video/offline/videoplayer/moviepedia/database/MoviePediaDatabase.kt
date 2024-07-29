package com.video.offline.videoplayer.moviepedia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.video.offline.videoplayer.moviepedia.database.models.MediaImage
import com.video.offline.videoplayer.moviepedia.database.models.MediaMetadata
import com.video.offline.videoplayer.moviepedia.database.models.MediaPersonJoin
import com.video.offline.videoplayer.moviepedia.database.models.Person
import org.videolan.tools.SingletonHolder

private const val DB_NAME = "moviepedia_database"

@Database(entities = [MediaMetadata::class, Person::class, MediaPersonJoin::class, MediaImage::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MoviePediaDatabase: RoomDatabase() {
    abstract fun mediaMetadataDao(): MediaMetadataDao
    abstract fun personDao(): PersonDao
    abstract fun mediaPersonActorJoinDao(): MediaPersonJoinDao
    abstract fun mediaMedataDataFullDao(): MediaMetadataDataFullDao
    abstract fun mediaImageDao(): MediaImageDao

    companion object : SingletonHolder<MoviePediaDatabase, Context>({ buildDatabase(it.applicationContext) })
}

private fun buildDatabase(context: Context) = Room.databaseBuilder(
        context.applicationContext,
        MoviePediaDatabase::class.java, DB_NAME
).build()
