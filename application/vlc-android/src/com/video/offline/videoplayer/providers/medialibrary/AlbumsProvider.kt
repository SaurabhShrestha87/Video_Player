package com.video.offline.videoplayer.providers.medialibrary

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Album
import org.videolan.medialibrary.interfaces.media.Artist
import org.videolan.medialibrary.interfaces.media.Genre
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.tools.Settings
import com.video.offline.videoplayer.viewmodels.SortableModel

class AlbumsProvider(val parent : MediaLibraryItem?, context: Context, model: SortableModel) : MedialibraryProvider<Album>(context, model) {

    override val sortKey = "${super.sortKey}_${parent?.javaClass?.simpleName}"
    override fun canSortByDuration() = true
    override fun canSortByReleaseDate() = true
    override fun canSortByArtist() = true
    override fun canSortByInsertionDate()= true

    init {
        sort = Settings.getInstance(context).getInt(sortKey, if (parent is Artist) Medialibrary.SORT_RELEASEDATE else Medialibrary.SORT_DEFAULT)
        desc = Settings.getInstance(context).getBoolean("${sortKey}_desc", false)
        onlyFavorites = Settings.getInstance(context).getBoolean("${sortKey}_only_favs", false)
    }

    override fun getAll() : Array<Album> = when (parent) {
        is Artist -> parent.getAlbums(sort, desc, Settings.includeMissing, onlyFavorites)
        is Genre -> parent.getAlbums(sort, desc, Settings.includeMissing, onlyFavorites)
        else -> medialibrary.getAlbums(sort, desc, Settings.includeMissing, onlyFavorites)
    }

    override fun getPage(loadSize: Int, startposition: Int) : Array<Album> {
        val list = if (model.filterQuery == null) when(parent) {
            is Artist -> parent.getPagedAlbums(sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            is Genre -> parent.getPagedAlbums(sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            else -> medialibrary.getPagedAlbums(sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        } else when(parent) {
            is Artist -> parent.searchAlbums(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            is Genre -> parent.searchAlbums(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            else -> medialibrary.searchAlbum(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        }
        model.viewModelScope.launch { completeHeaders(list, startposition) }
        return list
    }

    override fun getTotalCount() = if (model.filterQuery == null) when(parent) {
        is Artist -> parent.albumsCount
        is Genre -> parent.albumsCount
        else -> medialibrary.albumsCount
    } else when (parent) {
        is Artist -> parent.searchAlbumsCount(model.filterQuery)
        is Genre -> parent.searchAlbumsCount(model.filterQuery)
        else -> medialibrary.getAlbumsCount(model.filterQuery)
    }
}