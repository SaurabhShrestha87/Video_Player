package com.video.offline.videoplayer.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import com.video.offline.videoplayer.util.TestUtil
import com.video.offline.videoplayer.util.getValue

@RunWith(AndroidJUnit4::class)
class ExternalSubDaoTest: DbTest() {
    private lateinit var database: MediaDatabase
    @Test fun insertTwoSubtitleForEachOfTwoMedias_GetShouldReturnTwoForEachOne() {
        val foo = "/storage/emulated/foo.mkv"
        val bar = "/storage/emulated/bar.mkv"

        val fakeFooSubtitles = TestUtil.createExternalSubsForMedia(foo, "foo", 2)
        val fakeBarSubtitles = TestUtil.createExternalSubsForMedia(bar, "bar", 2)

        fakeFooSubtitles.forEach {
            db.externalSubDao().insert(it)
        }

        fakeBarSubtitles.forEach {
            db.externalSubDao().insert(it)
        }

        /*===========================================================*/

        val fooSubtitles = getValue(db.externalSubDao().get(foo))
        assertThat(fooSubtitles.size, equalTo(2))
        assertThat(fooSubtitles, hasItem(fakeFooSubtitles[0]))
        assertThat(fooSubtitles, hasItem(fakeFooSubtitles[1]))

        val barSubtitles = getValue(db.externalSubDao().get(bar))
        assertThat(barSubtitles.size, equalTo(2))
        assertThat(barSubtitles, hasItem(fakeBarSubtitles[0]))
        assertThat(barSubtitles, hasItem(fakeBarSubtitles[1]))
    }

    @Test fun InsertTwoSubtitleForOneMedia_DeleteOne() {
        val foo = "/storage/emulated/foo.mkv"

        val fakeFooSubtitles = TestUtil.createExternalSubsForMedia(foo,  "foo",2)

        fakeFooSubtitles.forEach {
            db.externalSubDao().insert(it)
        }

        /*===========================================================*/

        db.externalSubDao().delete(fakeFooSubtitles[0].mediaPath, fakeFooSubtitles[0].idSubtitle)
        var fooSubtitles = getValue(db.externalSubDao().get(foo))
        assertThat(fooSubtitles, not(hasItem(fakeFooSubtitles[0])))
        assertThat(fooSubtitles, hasItem(fakeFooSubtitles[1]))
    }


    @Test fun InsertTwoSubtitleForOneMedia_DeleteAll() {
        val foo = "/storage/emulated/foo.mkv"

        val fakeFooSubtitles = TestUtil.createExternalSubsForMedia(foo, "foo",  2)

        fakeFooSubtitles.forEach {
            db.externalSubDao().insert(it)
        }

        /*===========================================================*/

        db.externalSubDao().delete(fakeFooSubtitles[0].mediaPath, fakeFooSubtitles[0].idSubtitle)
        db.externalSubDao().delete(fakeFooSubtitles[1].mediaPath, fakeFooSubtitles[1].idSubtitle)

        var fooSubtitles = getValue(db.externalSubDao().get(foo))
        assertThat(fooSubtitles.size, `is`(0))
    }
}