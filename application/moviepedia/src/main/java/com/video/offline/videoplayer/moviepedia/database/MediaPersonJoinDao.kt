package com.video.offline.videoplayer.moviepedia.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.video.offline.videoplayer.moviepedia.database.models.MediaPersonJoin
import com.video.offline.videoplayer.moviepedia.database.models.Person
import com.video.offline.videoplayer.moviepedia.database.models.PersonType

@Dao
interface MediaPersonJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(person: MediaPersonJoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersons(persons: List<MediaPersonJoin>)

    @Query("DELETE FROM media_person_join WHERE mediaId = :moviepediaId")
    fun removeAllFor(moviepediaId: String)

    @Query("SELECT * FROM media_person_join")
    fun getAll(): List<MediaPersonJoin>

    @Query("""
           SELECT * FROM media_metadata_person
           INNER JOIN media_person_join
           ON media_metadata_person.moviepedia_id=media_person_join.personId
           WHERE media_person_join.mediaId=:moviepediaId AND media_person_join.type=:type
           """)
    fun getActorsForMediaLive(moviepediaId: String, type: PersonType): LiveData<List<Person>>

    @Query("""
           SELECT * FROM media_metadata_person
           INNER JOIN media_person_join
           ON media_metadata_person.moviepedia_id=media_person_join.personId
           WHERE media_person_join.mediaId=:moviepediaId AND media_person_join.type=:type
           """)
    fun getActorsForMedia(moviepediaId: String, type: PersonType): List<Person>
}