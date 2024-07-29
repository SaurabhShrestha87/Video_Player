package com.video.offline.videoplayer.providers.medialibrary

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Artist
import org.videolan.tools.Settings
import com.video.offline.videoplayer.viewmodels.SortableModel

class ArtistsProvider(context: Context, model: SortableModel, var showAll: Boolean) : MedialibraryProvider<Artist>(context, model) {

    override fun getAll() : Array<Artist> = medialibrary.getArtists(showAll, sort, desc, Settings.includeMissing, onlyFavorites)

    override fun getPage(loadSize: Int, startposition: Int): Array<Artist> {
        val list = if (model.filterQuery == null) medialibrary.getPagedArtists(showAll, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        else medialibrary.searchArtist(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        model.viewModelScope.launch { completeHeaders(list, startposition) }
        return list
    }

    override fun getTotalCount() = if (model.filterQuery == null) medialibrary.getArtistsCount(showAll)
    else medialibrary.getArtistsCount(model.filterQuery)
}