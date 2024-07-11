package com.video.offline.videoplayer.medialibrary.stubs;

import android.os.Parcel;

import com.video.offline.videoplayer.medialibrary.interfaces.media.Bookmark;

public class StubBookmark extends Bookmark {

    private StubDataSource dt = StubDataSource.getInstance();

    public StubBookmark(long id, String name, String description, long mediaId, long time) {
        super(id, name, description, mediaId, time);
    }

    @Override
    public boolean setName(String name) {
        return false;
    }

    @Override
    public boolean updateDescription(String description) {
        return false;
    }

    @Override
    public boolean setNameAndDescription(String name, String description) {
        return false;
    }

    @Override
    public boolean move(long time) {
        return false;
    }

    public StubBookmark(Parcel in) {
        super(in);
    }

}
