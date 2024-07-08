package com.video.offline.videoplayer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.powermock.modules.junit4.PowerMockRunner
import com.video.offline.videoplayer.database.CustomDirectoryDao
import com.video.offline.videoplayer.database.MediaDatabase
import com.video.offline.videoplayer.mediadb.models.CustomDirectory
import com.video.offline.videoplayer.util.TestUtil
import com.video.offline.videoplayer.util.argumentCaptor
import com.video.offline.videoplayer.util.mock
import com.video.offline.videoplayer.util.uninitialized

@RunWith(PowerMockRunner::class)
class DirectoryRepositoryTest {
    private val customDirectoryDao = mock<CustomDirectoryDao>()
    private lateinit var directoryRepository: DirectoryRepository

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before fun init() {
        val db = mock<MediaDatabase>()
        `when`(db.customDirectoryDao()).thenReturn(customDirectoryDao)
        directoryRepository = DirectoryRepository(customDirectoryDao)
    }

    @Test
    fun insertTwoCustomDirectory_GetAllShouldReturnTwo() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(2)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<com.video.offline.videoplayer.mediadb.models.CustomDirectory>()
        verify(customDirectoryDao, times(2)).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(2))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))
        assertThat(inserted.allValues[1], `is`(fakeCustomDirectories[1]))


        `when`(customDirectoryDao.getAll()).thenReturn(fakeCustomDirectories)

        val customDirectories = directoryRepository.getCustomDirectories()
        verify(customDirectoryDao).getAll()
        assertThat(customDirectories.size, `is`(2))
    }

    @Test
    fun insertTwoCustomDirectory_DeleteOneShouldDeleteOne() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(2)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<com.video.offline.videoplayer.mediadb.models.CustomDirectory>()
        verify(customDirectoryDao, times(2)).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(2))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))
        assertThat(inserted.allValues[1], `is`(fakeCustomDirectories[1]))

        directoryRepository.deleteCustomDirectory(fakeCustomDirectories[0].path).join()

        val deleted = argumentCaptor<com.video.offline.videoplayer.mediadb.models.CustomDirectory>()
        verify(customDirectoryDao).delete(deleted.capture() ?: uninitialized())
        assertThat(deleted.value, `is`(fakeCustomDirectories[0]))
    }

    @Test
    fun insertOneCustomDirectory_CheckExistenceShouldBeTrue() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(1)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<com.video.offline.videoplayer.mediadb.models.CustomDirectory>()
        verify(customDirectoryDao).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(1))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))

        `when`(customDirectoryDao.get(fakeCustomDirectories[0].path)).thenReturn(fakeCustomDirectories)

        val bool = directoryRepository.customDirectoryExists(fakeCustomDirectories[0].path)
        assertTrue(bool)
    }

    @Test
    fun insertOneCustomDirectory_CheckExistenceForWrongPathShouldBeFalse() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(1)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<com.video.offline.videoplayer.mediadb.models.CustomDirectory>()
        verify(customDirectoryDao).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(1))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))

        `when`(customDirectoryDao.get(fakeCustomDirectories[0].path)).thenReturn(fakeCustomDirectories)

        val bool = directoryRepository.customDirectoryExists(fakeCustomDirectories[0].path+"foo")
        assertFalse(bool)
    }

}
