package com.video.offline.videoplayer;

import org.videolan.libvlc.interfaces.IMedia;

import java.util.ArrayList;
import java.util.List;

public class VlcMigrationHelper {
    public static List<IMedia.Track> getMediaTracks(IMedia media) {
        ArrayList<IMedia.Track> result = new ArrayList<>();
        for (int i = 0; i < media.getTrackCount(); ++i) {
            result.add(media.getTrack(i));
        }
        return result;
    }
}
