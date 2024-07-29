package com.video.offline.videoplayer.viewmodels.mobile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Playlist
import org.videolan.resources.KEY_AUDIO_CURRENT_TAB
import org.videolan.tools.KEY_ARTISTS_SHOW_ALL
import org.videolan.tools.Settings
import com.video.offline.videoplayer.gui.audio.AudioBrowserFragment
import com.video.offline.videoplayer.providers.medialibrary.*
import com.video.offline.videoplayer.viewmodels.MedialibraryViewModel

class AudioBrowserViewModel(context: Context) : MedialibraryViewModel(context) {

    var currentTab = Settings.getInstance(context).getInt(KEY_AUDIO_CURRENT_TAB, 0)
    val artistsProvider = ArtistsProvider(context, this,
            Settings.getInstance(context).getBoolean(KEY_ARTISTS_SHOW_ALL, false))
    val albumsProvider = AlbumsProvider(null, context, this)
    val tracksProvider = TracksProvider(null, context, this)
    val genresProvider = GenresProvider(context, this)
    private val playlistsProvider = PlaylistsProvider(context, this, Playlist.Type.Audio)
    override val providers = arrayOf(artistsProvider, albumsProvider, tracksProvider, genresProvider, playlistsProvider)
    val providersInCard = arrayOf(true, true, false, false, true)

    var showResumeCard = settings.getBoolean("audio_resume_card", true)
    val displayModeKeys = arrayOf("display_mode_audio_browser_artists", "display_mode_audio_browser_albums", "display_mode_audio_browser_track", "display_mode_audio_browser_genres", "display_mode_playlists_AudioOnly")


    init {
        watchAlbums()
        watchArtists()
        watchGenres()
        watchMedia()
        watchPlaylists()
        //Initial state coming from preferences and falling back to [providersInCard] hardcoded values
        for (i in displayModeKeys.indices) {
            providersInCard[i] = settings.getBoolean(displayModeKeys[i], providersInCard[i])
        }

    }

    override fun refresh() {
        artistsProvider.showAll = settings.getBoolean(KEY_ARTISTS_SHOW_ALL, false)
        viewModelScope.launch {
            if (currentTab < providers.size) providers[currentTab].awaitRefresh()
            for ((index, provider) in providers.withIndex()) {
                if (index != currentTab && provider.loading.hasObservers()) provider.awaitRefresh()
            }
        }
    }

    class Factory(val context: Context): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AudioBrowserViewModel(context.applicationContext) as T
        }
    }
}

internal fun AudioBrowserFragment.getViewModel() = ViewModelProvider(requireActivity(), AudioBrowserViewModel.Factory(requireContext())).get(AudioBrowserViewModel::class.java)