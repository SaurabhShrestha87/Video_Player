package com.video.offline.videoplayer.util

import android.app.Activity
import android.content.Context.ACCESSIBILITY_SERVICE
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.databinding.BindingAdapter
import org.videolan.medialibrary.interfaces.media.*
import org.videolan.medialibrary.media.HistoryItem
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.R
import com.video.offline.videoplayer.gui.helpers.TalkbackUtil

fun Activity.isTalkbackIsEnabled() = (getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager?)?.isTouchExplorationEnabled
        ?: false

@BindingAdapter("mediaContentDescription")
fun mediaDescription(v: View, media: MediaLibraryItem?) {
    if (media == null) return
    v.contentDescription = when (media) {
        is VideoGroup -> TalkbackUtil.getVideoGroup(v.context, media)
        is Album -> TalkbackUtil.getAlbum(v.context, media)
        is Artist -> TalkbackUtil.getArtist(v.context, media)
        is Folder -> TalkbackUtil.getFolder(v.context, media)
        is Genre -> TalkbackUtil.getGenre(v.context, media)
        is HistoryItem -> v.context.getString(R.string.talkback_history_item)
        is Playlist -> TalkbackUtil.getPlaylist(v.context, media)
        is MediaWrapper -> when (media.type) {
            MediaWrapper.TYPE_VIDEO -> TalkbackUtil.getVideo(v.context, media)
            MediaWrapper.TYPE_AUDIO -> TalkbackUtil.getAudioTrack(v.context, media)
            MediaWrapper.TYPE_STREAM -> TalkbackUtil.getStream(v.context, media)
            MediaWrapper.TYPE_DIR, MediaWrapper.TYPE_SUBTITLE, MediaWrapper.TYPE_PLAYLIST-> TalkbackUtil.getDir(v.context, media, false)
                MediaWrapper.TYPE_ALL -> TalkbackUtil.getAll(media)
            else -> throw NotImplementedError("Media type not found: ${media.type}")
        }
        else -> throw NotImplementedError("Unknown item type")
    }
}
