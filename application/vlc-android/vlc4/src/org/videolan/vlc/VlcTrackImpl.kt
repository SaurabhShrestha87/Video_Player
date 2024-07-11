package com.video.offline.videoplayer

import org.videolan.libvlc.interfaces.IMedia
import com.video.offline.videoplayer.gui.dialogs.adapters.VlcTrack

class VlcTrackImpl : VlcTrack {
    lateinit var mediaTrack: IMedia.Track

    constructor(track: IMedia.Track) {
        this.mediaTrack = track
    }

    override fun getId() = mediaTrack.id

    override fun getName() = mediaTrack.name
    override fun getWidth() = (mediaTrack as? IMedia.VideoTrack)?.width ?: 0
    override fun getHeight() = (mediaTrack as? IMedia.VideoTrack)?.height ?: 0
    override fun getProjection() = (mediaTrack as? IMedia.VideoTrack)?.projection ?: 0 //todo good default value?
    override fun getFrameRateDen() = (mediaTrack as? IMedia.VideoTrack)?.frameRateDen ?: 0 //todo good default value?
    override fun getFrameRateNum() = (mediaTrack as? IMedia.VideoTrack)?.frameRateNum ?: 0 //todo good default value?

}