package com.video.offline.videoplayer.viewmodels.mobile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.videolan.medialibrary.interfaces.media.Album
import org.videolan.medialibrary.interfaces.media.Artist
import org.videolan.medialibrary.media.MediaLibraryItem
import com.video.offline.videoplayer.gui.audio.AudioAlbumsSongsFragment
import com.video.offline.videoplayer.providers.medialibrary.AlbumsProvider
import com.video.offline.videoplayer.providers.medialibrary.TracksProvider
import com.video.offline.videoplayer.viewmodels.MedialibraryViewModel

class AlbumSongsViewModel(context: Context, val parent: MediaLibraryItem) : MedialibraryViewModel(context) {

    val albumsProvider = AlbumsProvider(parent, context, this)
    val tracksProvider = TracksProvider(parent, context, this)
    override val providers = arrayOf(albumsProvider, tracksProvider)
    val providersInCard = arrayOf(true, false)
    val displayModeKeys = arrayOf("display_mode_albums_song_albums", "display_mode_albums_song_tracks")

    init {
        when (parent) {
            is Artist -> watchArtists()
            is Album -> watchAlbums()
            else -> watchMedia()
        }
        //Initial state coming from preferences and falling back to [providersInCard] hardcoded values
        for (i in displayModeKeys.indices) {
            providersInCard[i] = settings.getBoolean(displayModeKeys[i], providersInCard[i])
        }
    }

    class Factory(val context: Context, val parent: MediaLibraryItem): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AlbumSongsViewModel(context.applicationContext, parent) as T
        }
    }
}

internal fun AudioAlbumsSongsFragment.getViewModel(item : MediaLibraryItem) = ViewModelProvider(requireActivity(), AlbumSongsViewModel.Factory(requireContext(), item)).get(AlbumSongsViewModel::class.java)
