package com.video.offline.videoplayer.viewmodels.browser

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.videolan.tools.CoroutineContextProvider
import org.videolan.tools.NetworkMonitor

class NetworkModel(context: Context, url: String? = null, coroutineContextProvider: CoroutineContextProvider = CoroutineContextProvider()) : BrowserModel(context, url, TYPE_NETWORK, true, coroutineContextProvider = coroutineContextProvider) {

    init {
        NetworkMonitor.getInstance(context).connectionFlow.onEach {
            if (it.connected) refresh()
            else dataset.clear()
        }.launchIn(viewModelScope)
    }

    class Factory(val context: Context, val url: String?): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NetworkModel(context.applicationContext, url) as T
        }
    }
}