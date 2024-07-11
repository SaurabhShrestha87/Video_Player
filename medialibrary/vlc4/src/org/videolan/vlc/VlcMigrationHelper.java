package com.video.offline.videoplayer;

import org.videolan.libvlc.interfaces.IMedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class VlcMigrationHelper {
    public static List<IMedia.Track> getMediaTracks(IMedia media) {
        IMedia.Track[] tracks = media.getTracks();
        if (tracks == null) {
            return new ArrayList<IMedia.Track>();
        }
        return Arrays.asList(tracks);
    }
}
