package org.videolan.moviepedia.repository

import android.content.Context
import kotlinx.coroutines.launch
import org.videolan.moviepedia.database.MoviePediaDatabase
import org.videolan.tools.IOScopedObject
import org.videolan.tools.SingletonHolder
import org.videolan.moviepedia.database.PersonDao
import org.videolan.moviepedia.database.models.Person

class PersonRepository(private val personDao: PersonDao) : IOScopedObject() {

    fun addPerson(person: Person) = launch {
        personDao.insert(person)
    }

    fun addPersonImmediate(person: Person) = personDao.insert(person)

    fun getAll() = personDao.getAll()

    fun deleteAll(personsToRemove: List<Person>) = personDao.deleteAll(personsToRemove)

    companion object : SingletonHolder<PersonRepository, Context>({ PersonRepository(MoviePediaDatabase.getInstance(it).personDao()) })
}
