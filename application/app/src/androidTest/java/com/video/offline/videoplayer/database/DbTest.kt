package com.video.offline.videoplayer.database

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit


abstract class DbTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val countingTaskExecutorRule = CountingTaskExecutorRule()
    private lateinit var _db: MediaDatabase
    val db: MediaDatabase
        get() = _db

    @Before
    fun initDb() {
        _db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getTargetContext(),
            MediaDatabase::class.java)
            .build()
    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        _db.close()
    }

}
