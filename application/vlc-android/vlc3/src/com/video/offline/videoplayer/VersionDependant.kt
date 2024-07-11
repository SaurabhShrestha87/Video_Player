package com.video.offline.videoplayer

import android.content.Context
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IMedia
import com.video.offline.videoplayer.gui.dialogs.adapters.VlcTrack

fun IMedia.getAudioTracks(): List<IMedia.AudioTrack> {
    val tracks = ArrayList<IMedia.AudioTrack>()
    for (i in 0 until trackCount) {
        val track = getTrack(i)
        if (track is IMedia.AudioTrack) tracks.add(track)
    }
    return tracks.toList()
}

fun IMedia.getAllTracks(): List<IMedia.Track> {
    val result = ArrayList<IMedia.Track>()
    for (i in 0 until trackCount) {
        result.add(getTrack(i))
    }
    return result
}


fun MediaPlayer.getSelectedVideoTrack(): VlcTrack? = currentVideoTrack?.let { VlcTrackImpl(it) }

fun MediaPlayer.getSelectedAudioTrack(): VlcTrack? {
    val currentTrackId = audioTrack
   audioTracks?.forEach {
       if (it.id == currentTrackId) return VlcTrackImpl(it)
   }
    return null
}

fun MediaPlayer.getSelectedSpuTrack(): VlcTrack? {
    val currentTrackId = spuTrack
    spuTracks?.forEach {
        if (it.id == currentTrackId) return VlcTrackImpl(it)
    }
    return null
}

fun MediaPlayer.setVideoTrack(index:String):Boolean {
    return setVideoTrack(index.toInt())
}
fun MediaPlayer.setAudioTrack(index:String):Boolean {
    return setAudioTrack(index.toInt())
}

fun MediaPlayer.setSpuTrack(index:String):Boolean {
    return setSpuTrack(index.toInt())
}

fun MediaPlayer.getAllAudioTracks(): Array<VlcTrack> = audioTracks.convertToVlcTrack()
fun MediaPlayer.getAllVideoTracks():Array<VlcTrack> = videoTracks.convertToVlcTrack()
fun MediaPlayer.getAllSpuTracks():Array<VlcTrack> = spuTracks.convertToVlcTrack()

fun Array<MediaPlayer.TrackDescription>?.convertToVlcTrack(): Array<VlcTrack> {
    if (this == null) return arrayOf()
    val newTracks = ArrayList<VlcTrack>()
    this.forEach {
        newTracks.add(VlcTrackImpl(it))
    }
    return newTracks.toTypedArray()
}

fun MediaPlayer.unselectTrackType(type: Int) {
    throw IllegalStateException("This is a VLC 4 only API. It should not be called by VLC 3")
}
fun getDisableTrack(context: Context) : VlcTrack {
    throw IllegalStateException("This is a VLC 4 only API. It should not be called by VLC 3")
}
