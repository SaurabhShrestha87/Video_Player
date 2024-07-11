package org.videolan.moviepedia.provider.datasources

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.moviepedia.database.models.MediaMetadataType
import org.videolan.moviepedia.database.models.MediaMetadataWithImages
import org.videolan.moviepedia.repository.MediaMetadataRepository

class MovieDataSourceFactory(private val context: Context, private val sort: Pair<Int, Boolean>, private val metadataType: MediaMetadataType) : DataSource.Factory<Int, MediaMetadataWithImages>() {
    private val dataSource = MutableLiveData<DataSource<Int, MediaMetadataWithImages>>()
    override fun create(): DataSource<Int, MediaMetadataWithImages> {
        val sortField = when (sort.first) {
            Medialibrary.SORT_DEFAULT -> "title"
            Medialibrary.SORT_RELEASEDATE -> "releaseDate"
            else -> "title"
        }
        val sortType = if (sort.second) "DESC" else "ASC"

        val newDataSource = MediaMetadataRepository.getInstance(context).getMoviePagedList(sortField, sortType, metadataType).mapByPage {
            //Inject ML medias to results
            val medialibrary = Medialibrary.getInstance()
            if (medialibrary.isStarted) it.forEach { episode ->
                if (episode.media == null) {
                    episode.metadata.mlId?.let { mlId ->
                        episode.media = medialibrary.getMedia(mlId)
                    }
                }
            }
            it
        }.create()
        dataSource.postValue(newDataSource)
        return newDataSource
    }
}