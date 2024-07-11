package com.video.offline.videoplayer.util

import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.media.MediaWrapper

object DummyMediaWrapperProvider {
    fun getDummyMediaWrapper(id: Long): MediaWrapper {
        if (id >= 0) throw IllegalArgumentException("Dummy MediaWrapper id must be < 0")
        return MLServiceLocator.getAbstractMediaWrapper(id, "dummy://Mrl", -1L, -1F, 18820L, MediaWrapper.TYPE_VIDEO,
                "", "", "", "",
                "", "", 416, 304, "", 0, -2,
                0, 0, 1509466228L, 0L, true, false, 1970, true, 1683711438317L)
    }
}