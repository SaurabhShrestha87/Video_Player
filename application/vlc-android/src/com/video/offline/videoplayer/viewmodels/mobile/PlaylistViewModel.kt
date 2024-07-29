package com.video.offline.videoplayer.viewmodels.mobile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.Album
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.Playlist
import org.videolan.medialibrary.media.MediaLibraryItem
import com.video.offline.videoplayer.gui.HeaderMediaListActivity
import com.video.offline.videoplayer.providers.medialibrary.MedialibraryProvider
import com.video.offline.videoplayer.providers.medialibrary.TracksProvider
import com.video.offline.videoplayer.viewmodels.MedialibraryViewModel
import java.util.ArrayList

class PlaylistViewModel(context: Context, private val initialPlaylist: MediaLibraryItem) : MedialibraryViewModel(context) {

    val tracksProvider = TracksProvider(initialPlaylist, context, this)
    override val providers : Array<MedialibraryProvider<out MediaLibraryItem>> = arrayOf(tracksProvider)
    var playlistLiveData: MutableLiveData<MediaLibraryItem> = MutableLiveData()

    val playlist:MediaLibraryItem?
        get() = playlistLiveData.value

    init {
        when (initialPlaylist) {
            is Playlist -> {
                watchPlaylists()
                watchMedia()
            }
            is Album -> {
                watchAlbums()
                watchMedia()
            }
            else -> watchMedia()
        }
        viewModelScope.registerCallBacks { refresh() }
        viewModelScope.launch {
            refreshPlaylistItem()
        }
    }

    override fun refresh() {
        viewModelScope.launch {
            refreshPlaylistItem()
            super.refresh()
        }
    }

    private suspend fun refreshPlaylistItem() {
        withContext(Dispatchers.IO) {
            when (initialPlaylist) {
                is Album -> playlistLiveData.postValue(medialibrary.getAlbum(initialPlaylist.id))
                is Playlist -> playlistLiveData.postValue(medialibrary.getPlaylist(initialPlaylist.id, true, false))
            }
        }
    }

    class Factory(val context: Context, val playlist: MediaLibraryItem): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PlaylistViewModel(context.applicationContext, playlist) as T
        }
    }

    suspend fun rename(media: MediaWrapper, name:String) {
        withContext(Dispatchers.IO) { (media as? MediaWrapper)?.rename(name) }
        refresh()
    }

    suspend fun toggleFavorite() = withContext(Dispatchers.IO) {
        playlist?.let { it.setFavorite(!it.isFavorite) }
    }
}

internal fun HeaderMediaListActivity.getViewModel(playlist: MediaLibraryItem) = ViewModelProvider(this, PlaylistViewModel.Factory(this, playlist)).get(PlaylistViewModel::class.java)