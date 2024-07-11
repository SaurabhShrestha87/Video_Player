package com.video.offline.videoplayer.medialibrary.media;

import android.os.Parcel;

import com.video.offline.videoplayer.medialibrary.interfaces.Medialibrary;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Bookmark;

@SuppressWarnings("JniMissingFunction")
public class BookmarkImpl extends Bookmark {

    public BookmarkImpl(long id, String name, String description, long mediaId, long time) {
        super(id, name,description, mediaId, time);
    }

    public BookmarkImpl(Parcel in) {
        super(in);
    }

    @Override
    public boolean setName(String name) {
        this.setTitle(name);
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetName(ml, mId, name);
    }

    @Override
    public boolean updateDescription(String description) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetDescription(ml, mId, description);
    }

    @Override
    public boolean setNameAndDescription(String name, String description) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetNameAndDescription(ml, mId, name, description);
    }

    @Override
    public boolean move(long time) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeMove(ml, mId, time);
    }

    private native boolean nativeSetName(Medialibrary ml, long mId, String name);
    private native boolean nativeSetDescription(Medialibrary ml, long mId, String description);
    private native boolean nativeSetNameAndDescription(Medialibrary ml, long mId, String name, String description);
    private native boolean nativeMove(Medialibrary ml, long mId, long time);
}
