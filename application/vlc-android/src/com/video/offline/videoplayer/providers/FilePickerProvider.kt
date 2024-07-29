package com.video.offline.videoplayer.providers

import android.content.Context
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.util.MediaBrowser
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.tools.livedata.LiveDataset
import com.video.offline.videoplayer.util.isSoundFont

class FilePickerProvider(context: Context, dataset: LiveDataset<MediaLibraryItem>, url: String?, showDummyCategory: Boolean = false, private val pickerType:PickerType = PickerType.SUBTITLE) : FileBrowserProvider(context, dataset, url, true, showDummyCategory, Medialibrary.SORT_FILENAME, false) {

    override fun getFlags(interact : Boolean) = if (interact) MediaBrowser.Flag.NoSlavesAutodetect
    else MediaBrowser.Flag.Interact or MediaBrowser.Flag.NoSlavesAutodetect

    override fun initBrowser() {
        super.initBrowser()
        mediabrowser?.setIgnoreFileTypes("db,nfo,ini,jpg,jpeg,ljpg,gif,png,pgm,pgmyuv,pbm,pam,tga,bmp,pnm,xpm,xcf,pcx,tif,tiff,lbm,sfv")
    }

    override suspend fun findMedia(media: IMedia) = MLServiceLocator.getAbstractMediaWrapper(media)?.takeIf { mw ->
        mw.type == MediaWrapper.TYPE_DIR || (pickerType == PickerType.SUBTITLE && mw.type == MediaWrapper.TYPE_SUBTITLE) || (pickerType == PickerType.SOUNDFONT && mw.uri.isSoundFont())
    }

    override fun computeHeaders(value: List<MediaLibraryItem>) {}

    override fun parseSubDirectories(list : List<MediaLibraryItem>?) {}
}


enum class PickerType {
    SUBTITLE, SOUNDFONT
}
