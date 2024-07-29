package com.video.offline.videoplayer.providers.medialibrary

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Playlist
import org.videolan.tools.Settings
import com.video.offline.videoplayer.viewmodels.SortableModel

class PlaylistsProvider(context: Context, model: SortableModel, val type: Playlist.Type) : MedialibraryProvider<Playlist>(context, model) {

    override fun getAll() : Array<Playlist> = medialibrary.getPlaylists(type, sort, desc, Settings.includeMissing, onlyFavorites)

    override fun getPage(loadSize: Int, startposition: Int)  : Array<Playlist> {
        val list = if (model.filterQuery == null) medialibrary.getPagedPlaylists(type, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        else medialibrary.searchPlaylist(model.filterQuery, type, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        model.viewModelScope.launch { completeHeaders(list, startposition) }
        return list
    }

    override fun getTotalCount() = if (model.filterQuery == null) medialibrary.playlistsCount else medialibrary.getPlaylistsCount(model.filterQuery)
}