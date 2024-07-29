package com.video.offline.videoplayer.providers.medialibrary

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.tools.Settings
import com.video.offline.videoplayer.media.getAll
import com.video.offline.videoplayer.viewmodels.SortableModel

class VideosProvider(val folder : Folder?, val group: VideoGroup?, context: Context, model: SortableModel) : MedialibraryProvider<MediaWrapper>(context, model){

    override fun canSortByFileNameName() = true
    override fun canSortByDuration() = true
    override fun canSortByLastModified() = folder == null
    override fun canSortByInsertionDate() = group == null

    override fun getTotalCount() = if (model.filterQuery == null) when {
        folder !== null -> folder.mediaCount(Folder.TYPE_FOLDER_VIDEO)
        group !== null -> group.mediaCount()
        else -> medialibrary.videoCount
    } else when {
        folder !== null -> folder.searchTracksCount(model.filterQuery, Folder.TYPE_FOLDER_VIDEO)
        group !== null -> group.searchTracksCount(model.filterQuery)
        else -> medialibrary.getVideoCount(model.filterQuery)
    }

    override fun getPage(loadSize: Int, startposition: Int): Array<MediaWrapper> {
        val list = if (model.filterQuery == null) when {
            folder !== null -> folder.media(Folder.TYPE_FOLDER_VIDEO, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            group !== null -> group.media(sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            else -> medialibrary.getPagedVideos(sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        } else when {
            folder !== null -> folder.searchTracks(model.filterQuery, Folder.TYPE_FOLDER_VIDEO, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            group !== null -> group.searchTracks(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
            else -> medialibrary.searchVideo(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        }
        model.viewModelScope.launch { completeHeaders(list, startposition) }
        return list
    }

    override fun getAll(): Array<MediaWrapper> = when {
        folder !== null -> folder.getAll(Folder.TYPE_FOLDER_VIDEO, sort, desc, Settings.includeMissing).toTypedArray()
        group !== null -> group.getAll(sort, desc, Settings.includeMissing, onlyFavorites).toTypedArray()
        else -> medialibrary.getVideos(sort, desc, Settings.includeMissing, onlyFavorites)
    }
}
