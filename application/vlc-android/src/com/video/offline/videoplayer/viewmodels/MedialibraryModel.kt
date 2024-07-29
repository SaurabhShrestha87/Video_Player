package com.video.offline.videoplayer.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.tools.CoroutineContextProvider

abstract class MedialibraryModel<T : MediaLibraryItem>(context: Context, coroutineContextProvider: CoroutineContextProvider) : BaseModel<T>(context, coroutineContextProvider), ICallBackHandler by CallBackDelegate() {

    init {
        @Suppress("LeakingThis")
        viewModelScope.registerCallBacks { refresh() }
    }

    override fun onCleared() {
        releaseCallbacks()
        super.onCleared()
    }
}
