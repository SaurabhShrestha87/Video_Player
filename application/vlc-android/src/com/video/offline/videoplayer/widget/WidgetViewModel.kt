package com.video.offline.videoplayer.widget

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import org.videolan.vlc.mediadb.models.Widget
import com.video.offline.videoplayer.repository.WidgetRepository

class WidgetViewModel(context: Context, id:Int) : AndroidViewModel(context.applicationContext as Application) {
    private val widgetRepository = WidgetRepository.getInstance(context)

    val widget: LiveData<Widget> = widgetRepository.getWidgetFlow(id).asLiveData(viewModelScope.coroutineContext)

    suspend fun create(context:Context, appWidgetId:Int) {
        widgetRepository.createNew(context, appWidgetId)
    }

    class Factory(val context: Context, private val id:Int) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return WidgetViewModel(context.applicationContext ,id) as T
        }
    }
}