package com.video.offline.videoplayer.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.resources.AppContextProvider
import org.videolan.resources.TYPE_NETWORK_FAV
import org.videolan.tools.Settings
import com.video.offline.videoplayer.database.helpers.*
import com.video.offline.videoplayer.util.TestUtil
import com.video.offline.videoplayer.util.getValue

private const val TEST_DB_NAME = "test-db"

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            MediaDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test fun migrateFrom26() {
        // Slave
        val slaveMedia1Path = "/storage/emulated/0/Android/data/com.video.offline.videoplayer.debug/files/subs/file1.mkv"
        val slaveMedia1UriFa = "file:///storage/emulated/0/Android/data/com.video.offline.videoplayer.debug/files/subs/file1.fa.srt"
        // External Sub
        val exSubMedia1Name = "file1.mkv"
        val exSubMedisubsFolder = "/storage/emulated/0/Android/data/com.video.offline.videoplayer.debug/files/subs/"
        val exSubfile1Sub1 = "${exSubMedisubsFolder}file1.eng.srt"
        // Favs
        val favUri = "/storage/emulated/0/Android/data/com.video.offline.videoplayer.debug/files/subs/file1.mkv".toUri()
        val favTitle = "test1"

        val sqliteTestDbOpenHelper = SqliteTestDbOpenHelper(InstrumentationRegistry.getTargetContext(), TEST_DB_NAME, 26)
        createSlavesTable(sqliteTestDbOpenHelper)
        createExternalSubsTable_V26(sqliteTestDbOpenHelper)
        createNetworkFavsTable(sqliteTestDbOpenHelper)

        // Insert Data before migration
        saveSlave(slaveMedia1Path, IMedia.Slave.Type.Subtitle, 2, slaveMedia1UriFa, sqliteTestDbOpenHelper)
        saveExSubtitle(exSubfile1Sub1, exSubMedia1Name, sqliteTestDbOpenHelper)
        saveNetworkFavItem(favUri, favTitle, null, sqliteTestDbOpenHelper)

        migrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 29, true, migration_26_27, migration_27_28, migration_28_29)

        val migratedDb = getMigratedRoomDatabase(migration_26_27, migration_27_28, migration_28_29)

        val slave: com.video.offline.videoplayer.mediadb.models.Slave = migratedDb.slaveDao().get(slaveMedia1Path)[0]
        val exSub: List<com.video.offline.videoplayer.mediadb.models.ExternalSub> = getValue(migratedDb.externalSubDao().get(exSubMedia1Name))
        val fav: com.video.offline.videoplayer.mediadb.models.BrowserFav = migratedDb.browserFavDao().get(favUri)[0]

        assertEquals(slave.mediaPath, slaveMedia1Path)
        assertEquals(slave.type, IMedia.Slave.Type.Subtitle)
        assertEquals(slave.priority, 2)
        assertEquals(slave.uri, slaveMedia1UriFa)

        assertTrue(exSub.isEmpty())

        assertEquals(fav.uri, favUri)
        assertEquals(fav.title, favTitle)
        assertEquals(fav.iconUrl, null)
        assertEquals(fav.type, TYPE_NETWORK_FAV)

        clearDatabase(sqliteTestDbOpenHelper)
    }

    @Test fun migrateFrom27() {
        migrationTestHelper.createDatabase(TEST_DB_NAME, 27)
        val fakeCustomDirectories = TestUtil.createCustomDirectories(2)
        // 27_28 migration rule moves the data from prefs to room
        val prefCustomDirectories = fakeCustomDirectories.map { it.path }.reduce{ acc, path -> "$acc:$path" }
        Settings.getInstance(AppContextProvider.appContext).edit(commit = true) {
            putString("custom_paths", prefCustomDirectories)
        }

        migrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 28, true, migration_27_28, migration_28_29)
        val roomCustomDirectories = getMigratedRoomDatabase(migration_27_28, migration_28_29).customDirectoryDao().getAll()
        assertThat(roomCustomDirectories.size, `is`(2))
        assertThat(roomCustomDirectories, hasItem(fakeCustomDirectories[0]))
        assertThat(roomCustomDirectories, hasItem(fakeCustomDirectories[1]))
    }

    fun getMigratedRoomDatabase(vararg migrations:Migration): MediaDatabase {
        val database: MediaDatabase = Room.databaseBuilder(
                InstrumentationRegistry.getTargetContext(),
                MediaDatabase::class.java, TEST_DB_NAME)
                .addMigrations(*migrations)
                .build()

        // close the database and release any stream resources when the test finishes
        migrationTestHelper.closeWhenFinished(database)
        return database
    }
}