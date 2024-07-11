package com.video.offline.videoplayer

import org.videolan.libvlc.MediaPlayer
import  com.video.offline.videoplayer.gui.dialogs.adapters.VlcTrack
import  org.videolan.libvlc.interfaces.IMedia

class VlcTrackImpl : VlcTrack {
    private var mediaTrack: IMedia.Track? = null
    private var mediaplayerTrack: MediaPlayer.TrackDescription? = null

    constructor(track: MediaPlayer.TrackDescription) {
        this.mediaplayerTrack = track
    }

    constructor(track: IMedia.Track) {
        this.mediaTrack = track
    }

    override fun getId() = mediaplayerTrack?.id?.toString() ?: mediaTrack!!.id.toString()

    override fun getName() = mediaplayerTrack?.name ?: mediaTrack!!.description
    override fun getWidth() = (mediaplayerTrack as? IMedia.VideoTrack)?.width ?: 0
    override fun getHeight() = (mediaplayerTrack as? IMedia.VideoTrack)?.height ?: 0
    override fun getProjection() = (mediaplayerTrack as? IMedia.VideoTrack)?.projection ?: 0 //todo good default value?
    override fun getFrameRateDen() = (mediaplayerTrack as? IMedia.VideoTrack)?.frameRateDen ?: 0 //todo good default value?
    override fun getFrameRateNum() = (mediaplayerTrack as? IMedia.VideoTrack)?.frameRateNum ?: 0 //todo good default value?

}