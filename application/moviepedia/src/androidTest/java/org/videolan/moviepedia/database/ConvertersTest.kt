package com.video.offline.videoplayer.moviepedia.database

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertersTest {
    private val str = "upnp://http://[fe80::61a1:a5a4:c66:bc5d]:2869/u"
    private val uri = str.toUri()

    @Test fun uriToString() {
        assertEquals(str, Converters().uriToString(uri))
    }

    @Test fun stringToUri() {
        assertEquals(uri, Converters().stringToUri(str))
    }
}