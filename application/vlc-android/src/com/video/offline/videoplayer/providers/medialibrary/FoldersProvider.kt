package com.video.offline.videoplayer.providers.medialibrary

import android.content.Context
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.tools.Settings
import com.video.offline.videoplayer.viewmodels.SortableModel

class FoldersProvider(context: Context, model: SortableModel, val type: Int) : MedialibraryProvider<Folder>(context, model) {
    override fun getAll() : Array<Folder> = medialibrary.getFolders(type, sort, desc, Settings.includeMissing, onlyFavorites, getTotalCount(), 0)

    override fun getTotalCount() = if (model.filterQuery.isNullOrEmpty()) medialibrary.getFoldersCount(type) else medialibrary.getFoldersCount(model.filterQuery)

    override fun getPage(loadSize: Int, startposition: Int) : Array<Folder> = if (model.filterQuery.isNullOrEmpty()) {
        medialibrary.getFolders(type, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition).also { completeHeaders(it, startposition) }
    } else {
        medialibrary.searchFolders(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
    }.also { if (Settings.showTvUi) completeHeaders(it, startposition) }
}