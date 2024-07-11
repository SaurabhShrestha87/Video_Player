package org.videolan.moviepedia.provider

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import org.videolan.moviepedia.database.models.MediaMetadataType
import org.videolan.moviepedia.database.models.MediaMetadataWithImages
import org.videolan.moviepedia.provider.datasources.MovieDataSourceFactory

class MediaScrapingMovieProvider(private val context: Context, private val mediaType: MediaMetadataType) : MediaScrapingProvider(context) {

    override var pagedList: LiveData<PagedList<MediaMetadataWithImages>> = sortQuery.switchMap { input ->
        val movieDataSourceFactory = MovieDataSourceFactory(context, input, mediaType)
        //todo moviepedia set the right values
        val pagedListConfig = PagedList.Config.Builder()
                .setInitialLoadSizeHint(1)
                .setPageSize(20).build()
        LivePagedListBuilder(movieDataSourceFactory, pagedListConfig)
                .build()
    }.also {
        it.observeForever {
            //todo moviepedia find a better way to generate the headers. Typically, this implementation reloads the headers for the whole list instead of doing it only for the diff
            completeHeaders(it.toTypedArray(), 0)
            //todo moviepedia find a better way to generate laoding value
            loading.postValue(false)
        }
    }
}