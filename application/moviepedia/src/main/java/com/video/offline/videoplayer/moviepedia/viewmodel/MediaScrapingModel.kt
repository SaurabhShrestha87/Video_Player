package com.video.offline.videoplayer.moviepedia.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.video.offline.videoplayer.moviepedia.MediaScraper
import com.video.offline.videoplayer.moviepedia.models.resolver.ResolverMedia
import com.video.offline.videoplayer.moviepedia.models.resolver.ResolverResult
import videolan.org.commontools.LiveEvent

class MediaScrapingModel : ViewModel() {

    val apiResult: MutableLiveData<ResolverResult> = MutableLiveData()
    private val mediaResult: MutableLiveData<ResolverMedia> = MutableLiveData()
    private val repo = MediaScraper.mediaResolverApi
    val exceptionLiveData = LiveEvent<Exception?>()

    private var searchJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }
    private var mediaJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    fun search(query: String) {
        searchJob = viewModelScope.launch {
            try {
                apiResult.value = MediaScraper.mediaResolverApi.searchTitle(query)
            } catch (e: Exception) {
                exceptionLiveData.value = e
            }
        }
    }

    fun search(uri: Uri) {
        searchJob = viewModelScope.launch {
            try {
                apiResult.value = MediaScraper.mediaResolverApi.searchMedia(uri)
            } catch (e: Exception) {
                exceptionLiveData.value = e
            }
        }
    }

    fun getMedia(mediaId: String) {
        mediaJob = viewModelScope.launch {
            try {
                mediaResult.value = repo.getMedia(mediaId)
            } catch (e: Exception) {
                exceptionLiveData.value = e
            }
        }
    }
}