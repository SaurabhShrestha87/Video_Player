package com.video.offline.videoplayer.util

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * https://github.com/googlesamples/android-sunflower/blob/ee23fbc7608ea99a6fdd008885f0ae09d842dd47/app/src/androidTest/java/com/google/samples/apps/sunflower/utilities/LiveDataTestUtil.kt*
 *
 * Helper method for testing LiveData objects, from
 * https://github.com/googlesamples/android-architecture-components.
 *
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    liveData.observeForever { o ->
        data[0] = o
        latch.countDown()
    }
    latch.await(2, TimeUnit.SECONDS)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}

@Throws(InterruptedException::class)
fun <T> getValue(liveData: Flow<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    liveData.onEach { o ->
        data[0] = o
        latch.countDown()
    }
    latch.await(2, TimeUnit.SECONDS)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}