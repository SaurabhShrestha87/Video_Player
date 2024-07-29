package com.video.offline.videoplayer.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import com.video.offline.videoplayer.util.TestUtil
import com.video.offline.videoplayer.util.getValue


@RunWith(AndroidJUnit4::class)
class BrowserFavDaoTest: DbTest() {

    @Test fun insertTwoNetworkAndOneLocal_GetAllShouldReturnThreeFav() {
        val fakeNetworkFavs = TestUtil.createNetworkFavs(2)
        val fakeLocalFav = TestUtil.createLocalFavs(1)[0]

        fakeNetworkFavs.forEach { db.browserFavDao().insert(it) }
        db.browserFavDao().insert(fakeLocalFav)

        /*===========================================================*/

        val browsersFavs = getValue(db.browserFavDao().getAll())

        assertThat(browsersFavs.size, `is`(3))
        assertThat(browsersFavs, hasItem(fakeNetworkFavs[0]))
        assertThat(browsersFavs, hasItem(fakeNetworkFavs[1]))
        assertThat(browsersFavs, hasItem(fakeLocalFav))
    }

    @Test fun insertTwoNetworkAndOneLocal_GetNetworkFavsShouldReturnTwoFav() {
        val fakeNetworkFavs = TestUtil.createNetworkFavs(2)
        val fakeLocalFavs = TestUtil.createLocalFavs(1)[0]

        fakeNetworkFavs.forEach { db.browserFavDao().insert(it) }
        db.browserFavDao().insert(fakeLocalFavs)

        /*===========================================================*/

        val networkFavs = getValue(db.browserFavDao().getAllNetworkFavs())

        assertThat(networkFavs.size, equalTo(2))
        assertThat(networkFavs, hasItem(fakeNetworkFavs[0]))
        assertThat(networkFavs, hasItem(fakeNetworkFavs[1]))
    }


    @Test fun insertTwoNetworkAndTwoLocal_GetLocalFavsShouldReturnTwoFav() {
        val fakeNetworkFavs = TestUtil.createNetworkFavs(2)
        val fakeLocalFavs = TestUtil.createLocalFavs(2)

        fakeNetworkFavs.forEach { db.browserFavDao().insert(it) }
        fakeLocalFavs.forEach { db.browserFavDao().insert(it)}

        /*===========================================================*/

        val localFavs = getValue(db.browserFavDao().getAllLocalFavs())

        assertThat(localFavs.size, `is`(2))
        assertThat(localFavs, hasItem(localFavs[0]))
        assertThat(localFavs, hasItem(localFavs[1]))
    }

    @Test fun insertTwoNetworkAndTwoLocal_GetFavByUriShouldReturnOneFav() {
        val fakeNetworkFavs = TestUtil.createNetworkFavs(2)
        val fakeLocalFavs = TestUtil.createLocalFavs(2)

        fakeNetworkFavs.forEach { db.browserFavDao().insert(it) }
        fakeLocalFavs.forEach { db.browserFavDao().insert(it)}

        /*===========================================================*/

        val fav = db.browserFavDao().get(fakeNetworkFavs[0].uri)

        assertThat(fav.size, `is`(1))
        assertThat(fav[0], `is`(fakeNetworkFavs[0]))

    }

    @Test fun insertTwoNetworkAndTwoLocal_DeleteOne() {
        val fakeNetworkFavs = TestUtil.createNetworkFavs(2)
        val fakeLocalFavs = TestUtil.createLocalFavs(2)

        fakeNetworkFavs.forEach { db.browserFavDao().insert(it) }
        fakeLocalFavs.forEach { db.browserFavDao().insert(it)}

        /*===========================================================*/

        db.browserFavDao().delete(fakeNetworkFavs[0].uri)
        val favs = getValue(db.browserFavDao().getAll())

        assertThat(favs.size, `is`(3))
        assertThat(favs, not(hasItem(fakeNetworkFavs[0])))
    }

    @Test fun insertTwoNetworkAndTwoLocal_DeleteAll() {
        val fakeNetworkFavs = TestUtil.createNetworkFavs(2)
        val fakeLocalFavs = TestUtil.createLocalFavs(2)

        fakeNetworkFavs.forEach { db.browserFavDao().insert(it) }
        fakeLocalFavs.forEach { db.browserFavDao().insert(it)}

        /*===========================================================*/

        fakeLocalFavs.forEach { db.browserFavDao().delete(it.uri) }
        fakeNetworkFavs.forEach { db.browserFavDao().delete(it.uri) }
        val favs = getValue(db.browserFavDao().getAll())

        assertThat(favs.size, `is`(0))
    }
}