package com.video.offline.videoplayer.viewmodels.mobile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.videolan.medialibrary.interfaces.media.Playlist
import org.videolan.medialibrary.media.MediaLibraryItem
import com.video.offline.videoplayer.gui.PlaylistFragment
import com.video.offline.videoplayer.providers.medialibrary.MedialibraryProvider
import com.video.offline.videoplayer.providers.medialibrary.PlaylistsProvider
import com.video.offline.videoplayer.viewmodels.MedialibraryViewModel

class PlaylistsViewModel(context: Context, type: Playlist.Type) : MedialibraryViewModel(context) {
    val displayModeKey: String = "display_mode_playlists_$type"
    val provider = PlaylistsProvider(context, this, type)
    var providerInCard = true
    override val providers : Array<MedialibraryProvider<out MediaLibraryItem>> = arrayOf(provider)

    init {
        watchPlaylists()
        providerInCard = settings.getBoolean(displayModeKey, providerInCard)
    }

    class Factory(val context: Context, val type: Playlist.Type): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PlaylistsViewModel(context.applicationContext, type) as T
        }
    }
}

internal fun PlaylistFragment.getViewModel(type: Playlist.Type) =ViewModelProvider(this, PlaylistsViewModel.Factory(requireContext(), type)).get(PlaylistsViewModel::class.java)
