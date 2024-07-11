package org.videolan.moviepedia.provider

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.moviepedia.database.models.MediaMetadataWithImages
import org.videolan.moviepedia.getHeaderMoviepedia
import org.videolan.resources.util.HeaderProvider
import org.videolan.tools.Settings

abstract class MediaScrapingProvider(private val context: Context) : HeaderProvider() {

    abstract var pagedList: LiveData<PagedList<MediaMetadataWithImages>>
    val loading = MutableLiveData<Boolean>().apply { value = true }
    private val settings = Settings.getInstance(context)
    protected open val sortKey: String = this.javaClass.simpleName
    var sort = settings.getInt(sortKey, Medialibrary.SORT_DEFAULT)
    var desc = settings.getBoolean("${sortKey}_desc", false)
    var sortQuery = MutableLiveData<Pair<Int, Boolean>>().apply { value = Pair(sort, desc) }
    var isRefreshing = false
        private set(value) {
            loading.postValue(value)
            field = value
        }

    fun sort(sort: Int) {
        desc = when (this.sort) {
            Medialibrary.SORT_DEFAULT -> sort == Medialibrary.SORT_ALPHA
            sort -> !desc
            else -> false
        }
        this.sort = sort
        sortQuery.value = Pair(sort, desc)
        settings.edit {
            putInt(sortKey, sort)
            putBoolean("${sortKey}_desc", desc)
        }
    }

    fun refresh(): Boolean {
        if (isRefreshing || pagedList.value?.dataSource == null) return false
        privateHeaders.clear()
        if (!pagedList.value?.dataSource?.isInvalid!!) {
            isRefreshing = true
            pagedList.value?.dataSource?.invalidate()
        }
        return true
    }

    fun completeHeaders(list: Array<MediaMetadataWithImages>, startposition: Int) {
        for ((position, item) in list.withIndex()) {
            val previous = when {
                position > 0 -> list[position - 1]
                startposition > 0 -> pagedList.value?.getOrNull(startposition + position - 1)
                else -> null
            }
            getHeaderMoviepedia(context, sort, item.metadata, previous?.metadata)?.let {
                privateHeaders.put(startposition + position, it)
            }
        }
        (liveHeaders as MutableLiveData).postValue(privateHeaders.clone())
    }
}