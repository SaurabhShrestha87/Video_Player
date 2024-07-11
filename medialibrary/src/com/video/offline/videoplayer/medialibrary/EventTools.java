package com.video.offline.videoplayer.medialibrary;

import androidx.lifecycle.LiveData;

import com.video.offline.videoplayer.medialibrary.interfaces.media.MediaWrapper;

public final class EventTools {
    private static EventTools sInstance;
    public final LiveData<MediaWrapper> lastThumb = new SingleEvent<>();


    public static EventTools getInstance() {
        if (sInstance == null) {
            sInstance = new EventTools();
        }
        return sInstance;
    }

}