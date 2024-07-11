package com.video.offline.videoplayer

import android.content.Context
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IMedia
import com.video.offline.videoplayer.gui.dialogs.adapters.VlcTrack

fun IMedia.getAudioTracks(): List<IMedia.AudioTrack> =
    getTracks(IMedia.Track.Type.Audio)?.map { it as IMedia.AudioTrack }?.toList() ?: emptyList()

fun IMedia.getAllTracks() = tracks.toList()

fun MediaPlayer.getSelectedVideoTrack(): VlcTrack? {
    return getSelectedTrack(IMedia.Track.Type.Video)?.let { VlcTrackImpl(it) }
}

fun MediaPlayer.getSelectedAudioTrack(): VlcTrack? {
    return getSelectedTrack(IMedia.Track.Type.Audio)?.let { VlcTrackImpl(it) }
}

fun MediaPlayer.getSelectedSpuTrack(): VlcTrack? {
    return getSelectedTrack(IMedia.Track.Type.Text)?.let { VlcTrackImpl(it) }
}

fun MediaPlayer.getVideoTracksCount(): Int {
    return getTracks(IMedia.Track.Type.Video)?.size ?: 0
}

fun MediaPlayer.getAudioTracksCount(): Int {
    return getTracks(IMedia.Track.Type.Audio)?.size ?: 0
}

fun MediaPlayer.getSpuTracksCount(): Int {
    return getTracks(IMedia.Track.Type.Text)?.size ?: 0
}

fun MediaPlayer.setVideoTrack(index: String): Boolean {
    return selectTrack(index)
}

fun MediaPlayer.setAudioTrack(index: String): Boolean {
    return selectTrack(index)
}

fun MediaPlayer.setSpuTrack(index: String): Boolean {
    return selectTrack(index)
}

fun MediaPlayer.getAllAudioTracks(): Array<VlcTrack> =
    getTracks(IMedia.Track.Type.Audio)?.convertToVlcTrack() ?: arrayOf()

fun MediaPlayer.getAllVideoTracks(): Array<VlcTrack> =
    getTracks(IMedia.Track.Type.Video)?.convertToVlcTrack() ?: arrayOf()

fun MediaPlayer.getAllSpuTracks(): Array<VlcTrack> =
    getTracks(IMedia.Track.Type.Text)?.convertToVlcTrack() ?: arrayOf()

fun Array<IMedia.Track>.convertToVlcTrack(): Array<VlcTrack> {
    val newTracks = ArrayList<VlcTrack>()
    this.forEach {
        newTracks.add(VlcTrackImpl(it))
    }
    return newTracks.toTypedArray()
}

/**
 * Generates a fake track to be used as "Disable track" in the [TrackAdapter]
 *
 * @param context the context to use
 * @return a fake track
 */
fun getDisableTrack(context: Context) = object : VlcTrack {
    override fun getName() = context.getString(R.string.disable_track)

    override fun getId() = "-1"

    override fun getWidth() = 0

    override fun getHeight() = 0

    override fun getProjection() = 0

    override fun getFrameRateDen() = 0

    override fun getFrameRateNum() = 0
}
