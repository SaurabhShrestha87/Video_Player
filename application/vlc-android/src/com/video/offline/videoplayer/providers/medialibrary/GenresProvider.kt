package com.video.offline.videoplayer.providers.medialibrary

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Genre
import org.videolan.tools.Settings
import com.video.offline.videoplayer.viewmodels.SortableModel

class GenresProvider(context: Context, model: SortableModel) : MedialibraryProvider<Genre>(context, model)  {

    override fun getAll() : Array<Genre> = medialibrary.getGenres(sort, desc, Settings.includeMissing, onlyFavorites)

    override fun getPage(loadSize: Int, startposition: Int) : Array<Genre> {
        val list = if (model.filterQuery == null) medialibrary.getPagedGenres(sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        else medialibrary.searchGenre(model.filterQuery, sort, desc, Settings.includeMissing, onlyFavorites, loadSize, startposition)
        model.viewModelScope.launch { completeHeaders(list, startposition) }
        return list
    }

    override fun getTotalCount() = if (model.filterQuery == null) medialibrary.genresCount else medialibrary.getGenresCount(model.filterQuery)
}