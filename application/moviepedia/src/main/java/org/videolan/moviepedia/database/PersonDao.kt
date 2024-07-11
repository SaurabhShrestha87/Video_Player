package org.videolan.moviepedia.database

import androidx.room.*
import org.videolan.moviepedia.database.models.Person

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: Person)

    @Delete
    fun deleteAll(person: List<Person>)

    @Query("SELECT * from media_metadata_person where moviepedia_id = :id")
    fun getPerson(id: String): Person

    @Query("SELECT * from media_metadata_person")
    fun getAll(): List<Person>
}
