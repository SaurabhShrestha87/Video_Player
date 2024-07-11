package com.video.offline.videoplayer.medialibrary;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;

import com.video.offline.videoplayer.medialibrary.media.AlbumImpl;
import com.video.offline.videoplayer.medialibrary.media.FolderImpl;
import com.video.offline.videoplayer.medialibrary.media.GenreImpl;
import com.video.offline.videoplayer.medialibrary.media.MediaWrapperImpl;
import com.video.offline.videoplayer.medialibrary.media.VideoGroupImpl;
import org.videolan.libvlc.interfaces.IMedia;
import com.video.offline.videoplayer.medialibrary.interfaces.Medialibrary;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Album;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Artist;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Bookmark;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Folder;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Genre;
import com.video.offline.videoplayer.medialibrary.interfaces.media.MediaWrapper;
import com.video.offline.videoplayer.medialibrary.interfaces.media.Playlist;
import com.video.offline.videoplayer.medialibrary.interfaces.media.VideoGroup;
import com.video.offline.videoplayer.medialibrary.media.ArtistImpl;
import com.video.offline.videoplayer.medialibrary.media.BookmarkImpl;
import com.video.offline.videoplayer.medialibrary.media.PlaylistImpl;
import com.video.offline.videoplayer.medialibrary.stubs.StubAlbum;
import com.video.offline.videoplayer.medialibrary.stubs.StubArtist;
import com.video.offline.videoplayer.medialibrary.stubs.StubBookmark;
import com.video.offline.videoplayer.medialibrary.stubs.StubFolder;
import com.video.offline.videoplayer.medialibrary.stubs.StubGenre;
import com.video.offline.videoplayer.medialibrary.stubs.StubMediaWrapper;
import com.video.offline.videoplayer.medialibrary.stubs.StubMedialibrary;
import com.video.offline.videoplayer.medialibrary.stubs.StubPlaylist;
import com.video.offline.videoplayer.medialibrary.stubs.StubVideoGroup;

public class MLServiceLocator {

    private static LocatorMode sMode = LocatorMode.VLC_ANDROID;
    private static volatile Medialibrary instance;

    public static void setLocatorMode(LocatorMode mode) {
        if (instance != null && mode != sMode) {
            throw new IllegalStateException("LocatorMode must be set before Medialibrary initialization");
        }
        MLServiceLocator.sMode = mode;
    }

    public static LocatorMode getLocatorMode() {
        return MLServiceLocator.sMode;
    }

    public static String EXTRA_TEST_STUBS = "extra_test_stubs";

    public enum LocatorMode {
        VLC_ANDROID,
        TESTS,
    }

    public static synchronized Medialibrary getAbstractMedialibrary() {
        if (instance == null) {
            instance = sMode == LocatorMode.VLC_ANDROID ? new MedialibraryImpl() : new StubMedialibrary();
        }
        return instance;
    }

    // MediaWrapper
    public static MediaWrapper getAbstractMediaWrapper(long id, String mrl, long time, float position, long length,
                                                       int type, String title, String filename,
                                                       String artist, String genre, String album,
                                                       String albumArtist, int width, int height,
                                                       String artworkURL, int audio, int spu,
                                                       int trackNumber, int discNumber, long lastModified,
                                                       long seen, boolean isThumbnailGenerated, boolean isFavorite,
                                                       int releaseDate, boolean isPresent, long insertionDate) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new MediaWrapperImpl(id, mrl, time, position, length, type, title,
                    filename, artist, genre, album, albumArtist, width, height, artworkURL,
                    audio, spu, trackNumber, discNumber, lastModified, seen, isThumbnailGenerated,
                    isFavorite, releaseDate, isPresent, insertionDate);
        } else {
            return new StubMediaWrapper(id, mrl, time, position, length, type, title,
                    filename, artist, genre, album, albumArtist, width, height, artworkURL,
                    audio, spu, trackNumber, discNumber, lastModified, seen, isThumbnailGenerated, isFavorite, releaseDate, isPresent, insertionDate);
        }
    }

    public static MediaWrapper getAbstractMediaWrapper(Uri uri, long time, float position, long length, int type,
                                                       Bitmap picture, String title, String artist,
                                                       String genre, String album, String albumArtist,
                                                       int width, int height, String artworkURL,
                                                       int audio, int spu, int trackNumber,
                                                       int discNumber, long lastModified, long seen,
                                                       long insertionDate) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new MediaWrapperImpl(uri, time, position, length, type, picture, title, artist, genre,
                    album, albumArtist, width, height, artworkURL, audio, spu, trackNumber,
                    discNumber, lastModified, seen, false, insertionDate);
        } else {
            return new StubMediaWrapper(uri, time, position, length, type, picture, title, artist, genre,
                    album, albumArtist, width, height, artworkURL, audio, spu, trackNumber,
                    discNumber, lastModified, seen, false, insertionDate);
        }
    }

    public static MediaWrapper getAbstractMediaWrapper(Uri uri) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new MediaWrapperImpl(uri);
        } else {
            return new StubMediaWrapper(uri);
        }
    }

    public static MediaWrapper getAbstractMediaWrapper(IMedia media) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new MediaWrapperImpl(media);
        } else {
            return new StubMediaWrapper(media);
        }
    }

    public static MediaWrapper getAbstractMediaWrapper(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new MediaWrapperImpl(in);
        } else {
            return new StubMediaWrapper(in);
        }
    }

    //Artist
    public static Artist getAbstractArtist(long id, String name, String shortBio, String artworkMrl, String musicBrainzId, int albumsCount, int tracksCount, int presentTracksCount, boolean isFavorite) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new ArtistImpl(id, name, shortBio, artworkMrl, musicBrainzId, albumsCount, tracksCount, presentTracksCount, isFavorite);
        } else {
            return new StubArtist(id, name, shortBio, artworkMrl, musicBrainzId, albumsCount, tracksCount, presentTracksCount, isFavorite);
        }
    }

    public static Artist getAbstractArtist(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new ArtistImpl(in);
        } else {
            return new StubArtist(in);
        }
    }

    //Genre
    public static Genre getAbstractGenre(long id, String title, boolean isFavorite) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new GenreImpl(id, title, 0, 0, isFavorite);
        } else {
            return new StubGenre(id, title, 0, 0, isFavorite);
        }
    }

    public static Genre getAbstractGenre(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new GenreImpl(in);
        } else {
            return new StubGenre(in);
        }
    }

    //Album
    public static Album getAbstractAlbum(long id, String title, int releaseYear, String artworkMrl,
                                         String albumArtist, long albumArtistId, int nbTracks, int nbPresentTracks,
                                         long duration, boolean isFavorite) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new AlbumImpl(id, title, releaseYear, artworkMrl, albumArtist, albumArtistId,
                    nbTracks, nbPresentTracks, duration, isFavorite);
        } else {
            return new StubAlbum(id, title, releaseYear, artworkMrl, albumArtist, albumArtistId,
                    nbTracks, nbPresentTracks, duration, isFavorite);
        }
    }

    public static Album getAbstractAlbum(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new AlbumImpl(in);
        } else {
            return new StubAlbum(in);
        }
    }

    //FolderImpl
    public static Folder getAbstractFolder(long id, String name, String mrl, int count, boolean isFavorite) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new FolderImpl(id, name, mrl, count, isFavorite);
        } else {
            return new StubFolder(id, name, mrl, count, isFavorite);
        }
    }

    public static Folder getAbstractFolder(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new FolderImpl(in);
        } else {
            return new StubFolder(in);
        }
    }

    //BookmarkImpl
    public Bookmark getAbstractBookmark(long id, String name, String description, long mediaId, long time) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new BookmarkImpl(id, name, description, mediaId, time);
        } else {
            return new StubBookmark(id, name, description, mediaId, time);
        }
    }

    public static Bookmark getAbstractBookmark(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new BookmarkImpl(in);
        } else {
            return new StubBookmark(in);
        }
    }

    public static VideoGroup getAbstractVideoGroup(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new VideoGroupImpl(in);
        } else {
            return new StubVideoGroup(in);
        }
    }

    //Playlist
    public static Playlist getAbstractPlaylist(long id, String name, int trackCount, long duration, int nbVideo, int nbAudio, int nbUnknown, int nbDurationUnknown, boolean isFavorite) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new PlaylistImpl(id, name, trackCount, duration, nbVideo, nbAudio, nbUnknown, nbDurationUnknown, isFavorite);
        } else {
            return new StubPlaylist(id, name, trackCount, duration, nbVideo, nbAudio, nbUnknown, nbDurationUnknown, isFavorite);
        }
    }

    public static Playlist getAbstractPlaylist(Parcel in) {
        if (sMode == LocatorMode.VLC_ANDROID) {
            return new PlaylistImpl(in);
        } else {
            return new StubPlaylist(in);
        }
    }
}
