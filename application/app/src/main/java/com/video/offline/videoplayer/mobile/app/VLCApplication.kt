package com.video.offline.videoplayer.mobile.app

import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import org.videolan.libvlc.Dialog
import org.videolan.tools.BitmapCache
import com.video.offline.videoplayer.ArtworkProvider
import com.video.offline.videoplayer.util.DialogDelegate
import org.videolan.mobile.app.AppDelegate
import org.videolan.mobile.app.AppSetupDelegate

private const val TAG = "VLC/VLCApplication"

class VLCApplication : MultiDexApplication(), Dialog.Callbacks by DialogDelegate, AppDelegate by AppSetupDelegate() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        setupApplication()
        super.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appContextProvider.updateContext()
    }

    /**
     * Called when the overall system is running low on memory
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "System is running low on memory")
        BitmapCache.clear()
        ArtworkProvider.clear()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.w(TAG, "onTrimMemory, level: $level")
        BitmapCache.clear()
        ArtworkProvider.clear()
    }
}
