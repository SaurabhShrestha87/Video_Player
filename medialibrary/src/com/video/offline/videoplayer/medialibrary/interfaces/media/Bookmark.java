package com.video.offline.videoplayer.medialibrary.interfaces.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.video.offline.videoplayer.medialibrary.MLServiceLocator;
import com.video.offline.videoplayer.medialibrary.media.MediaLibraryItem;

abstract public class Bookmark extends MediaLibraryItem {
    public long mediaId;
    public long mTime;

    public Bookmark(long id, String name, String description, long mediaId, long time) {
        super(id, name);
        this.mediaId = mediaId;
        this.mTime = time;
        this.setDescription(description);
    }

    @Override
    public boolean setFavorite(boolean favorite) {
        return false;
    }

    @Override
    public MediaWrapper[] getTracks() {
        return new MediaWrapper[0];
    }

    @Override
    public int getTracksCount() {
        return 0;
    }

    @Override
    public int getItemType() {
        return TYPE_BOOKMARK;
    }


    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    abstract public boolean setName(String name);
    abstract public boolean updateDescription(String description);
    abstract public boolean setNameAndDescription(String name, String description);
    abstract public boolean move(long time);


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(mediaId);
    }

    public static Parcelable.Creator<Bookmark> CREATOR = new Parcelable.Creator<Bookmark>() {
        @Override
        public Bookmark createFromParcel(Parcel in) {
            return MLServiceLocator.getAbstractBookmark(in);
        }

        @Override
        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };

    public Bookmark(Parcel in) {
        super(in);
        this.mediaId = in.readLong();
        this.mTime = in.readLong();
    }

    public boolean equals(Bookmark other) {
        return mId == other.getId();
    }

    @Override
    public boolean equals(MediaLibraryItem other) {
        if (other instanceof Bookmark) return equals((Bookmark)other);
        return super.equals(other);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bookmark) return equals((Bookmark)obj);
        return super.equals(obj);
    }
}
