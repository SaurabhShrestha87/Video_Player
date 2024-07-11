package com.video.offline.videoplayer.gui.browser

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.video.offline.videoplayer.interfaces.IEventsHandler

interface BrowserContainer<T> : IEventsHandler<T> {
    fun containerActivity(): Activity
    fun getStorageDelegate():IStorageFragmentDelegate?

    val scannedDirectory: Boolean
    val mrl: String?
    val isRootDirectory: Boolean
    val isNetwork: Boolean
    val isFile: Boolean
    var inCards: Boolean
}

class BrowserContainerImpl<T>(
        override val scannedDirectory: Boolean,
        override val mrl: String?,
        override val isRootDirectory: Boolean,
        override val isNetwork: Boolean,
        override val isFile: Boolean,
        override var inCards: Boolean
) : BrowserContainer<T> {
    override fun containerActivity() = throw NotImplementedError()
    override fun getStorageDelegate(): IStorageFragmentDelegate? = null
    override fun onClick(v: View, position: Int, item: T) {}
    override fun onLongClick(v: View, position: Int, item: T) = false
    override fun onImageClick(v: View, position: Int, item: T) {}
    override fun onCtxClick(v: View, position: Int, item: T) {}
    override fun onUpdateFinished(adapter: RecyclerView.Adapter<*>) {}
    override fun onMainActionClick(v: View, position: Int, item: T) {}
    override fun onItemFocused(v: View, item: T) {}
}