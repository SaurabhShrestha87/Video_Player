package com.video.offline.videoplayer.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import com.video.offline.videoplayer.util.TestUtil

@RunWith(AndroidJUnit4::class)
class SlaveDaoTest: DbTest() {

    @Test fun insertTwoSubtitleSlave_GetShouldReturnJustLatOne() {
        val fakeSlaves = TestUtil.createSubtitleSlavesForMedia("foo", 2)
        fakeSlaves.forEach {
            db.slaveDao().insert(it)
        }

        /*===========================================================*/

        val slaves = db.slaveDao().get(fakeSlaves[0].mediaPath)
        assertThat(slaves.size, equalTo(1))
        assertThat(slaves, hasItem(fakeSlaves[1]))
    }
}
