/*****************************************************************************
 * VideosViewModel.kt
 *****************************************************************************
 * Copyright © 2019 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.video.offline.videoplayer.viewmodels.mobile

import android.content.Context
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.medialibrary.media.FolderImpl
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.tools.FORCE_PLAY_ALL_VIDEO
import org.videolan.tools.Settings
import org.videolan.tools.isStarted
import org.videolan.tools.retrieveParent
import com.video.offline.videoplayer.gui.CleanerFragment
import com.video.offline.videoplayer.gui.helpers.UiTools
import com.video.offline.videoplayer.gui.helpers.UiTools.addToPlaylist
import com.video.offline.videoplayer.gui.video.VideoGridFragment
import com.video.offline.videoplayer.gui.video.cleaner.CleanerListFragment
import com.video.offline.videoplayer.media.MediaUtils
import com.video.offline.videoplayer.media.PlaylistManager
import com.video.offline.videoplayer.media.getAll
import com.video.offline.videoplayer.providers.medialibrary.FoldersProvider
import com.video.offline.videoplayer.providers.medialibrary.MedialibraryProvider
import com.video.offline.videoplayer.providers.medialibrary.VideoGroupsProvider
import com.video.offline.videoplayer.providers.medialibrary.VideosProvider
import com.video.offline.videoplayer.viewmodels.MedialibraryViewModel
import java.io.File

class VideosViewModel(context: Context, type: VideoGroupingType, val folder: Folder?, val group: VideoGroup?) : MedialibraryViewModel(context) {

    var groupingType = type
        private set
    var provider = loadProvider()
        private set

    private fun loadProvider(): MedialibraryProvider<out MediaLibraryItem> = when (groupingType) {
        VideoGroupingType.NONE -> VideosProvider(folder, group, context, this)
        VideoGroupingType.FOLDER -> FoldersProvider(context, this, Folder.TYPE_FOLDER_VIDEO)
        VideoGroupingType.NAME -> VideoGroupsProvider(context, this)
    }

    override val providers: Array<MedialibraryProvider<out MediaLibraryItem>> = arrayOf(provider)

    internal fun changeGroupingType(type: VideoGroupingType) {
        if (groupingType == type) return
        groupingType = type
        provider = loadProvider()
        providers[0] = provider
        refresh()
    }

    init {
        watchMedia()
        watchMediaGroups()
        watchFolders()
    }

    class Factory(val context: Context, private val groupingType: VideoGroupingType, val folder: Folder? = null, val group: VideoGroup? = null) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return VideosViewModel(context.applicationContext, groupingType, folder, group) as T
        }
    }

    // Folders & Groups
    internal fun play(position: Int) = viewModelScope.launch {
        val item = provider.pagedList.value?.get(position) ?: return@launch
        withContext(Dispatchers.IO) {
            when (item) {
                is Folder -> item.getAll()
                is VideoGroup -> item.getAll()
                is MediaWrapper -> listOf(item)
                else -> null
            }
        }?.let { MediaUtils.openList(context, it, 0) }
    }

    internal fun append(position: Int) = viewModelScope.launch {
        val item = provider.pagedList.value?.get(position) ?: return@launch
        withContext(Dispatchers.IO) {
            when (item) {
                is Folder -> item.getAll()
                is VideoGroup -> item.getAll()
                else -> null
            }
        }?.let { MediaUtils.appendMedia(context, it) }
    }

    internal fun playFoldersSelection(selection: List<Folder>) = viewModelScope.launch {
        val list = selection.flatMap { it.getAll() }
        MediaUtils.openList(context, list, 0)
    }

    internal fun playFoldersSelectionBackground(selection: List<Folder>) = viewModelScope.launch {
        val list = selection.flatMap { folder1 -> folder1.getAll().onEach { it.addFlags(MediaWrapper.MEDIA_FORCE_AUDIO) } }
        MediaUtils.openList(context, list, 0)
    }

    internal fun addItemToPlaylist(activity: FragmentActivity, position: Int) = viewModelScope.launch {
        val item = provider.pagedList.value?.get(position) ?: return@launch
        withContext(Dispatchers.IO) {
            when (item) {
                is Folder -> item.getAll()
                is VideoGroup -> item.getAll()
                else -> null
            }
        }?.let { if (activity.isStarted()) activity.addToPlaylist(it) }
    }

    internal fun appendFoldersSelection(selection: List<Folder>) = viewModelScope.launch {
        val list = selection.flatMap { it.getAll() }
        MediaUtils.appendMedia(context, list)
    }

    internal fun playVideo(context: FragmentActivity?, mw: MediaWrapper, position: Int, fromStart: Boolean = false, forceAll:Boolean = false) {
        if (context === null) return
        if (!mw.isPresent) {
            UiTools.snackerMissing(context)
            return
        }
        mw.removeFlags(MediaWrapper.MEDIA_FORCE_AUDIO)
        PlaylistManager.playingAsAudio = false
        val settings = Settings.getInstance(context)
        if (!fromStart && (settings.getBoolean(FORCE_PLAY_ALL_VIDEO, Settings.tvUI) || forceAll)) {
            when(val prov = provider) {
                is VideosProvider -> MediaUtils.playAll(context, prov, position, false)
                is FoldersProvider -> MediaUtils.playAllTracks(context, prov, position, false)
                is VideoGroupsProvider -> MediaUtils.playAllTracks(context, prov, mw, false)
            }
        } else {
            if (fromStart) mw.addFlags(MediaWrapper.MEDIA_FROM_START)
            MediaUtils.openMedia(context, mw)
        }
    }

    internal fun playAll(activity: FragmentActivity?, position: Int = 0) {
        if (activity?.isStarted() == true) when (groupingType) {
            VideoGroupingType.NONE -> MediaUtils.playAll(activity, provider as VideosProvider, position, false)
            VideoGroupingType.FOLDER -> MediaUtils.playAllTracks(activity, (provider as FoldersProvider), position, false)
            VideoGroupingType.NAME -> MediaUtils.playAllTracks(activity, (provider as VideoGroupsProvider), null, false)
        }
    }

    internal fun playAudio(activity: FragmentActivity?, media: MediaWrapper) {
        if (activity == null) return
        media.addFlags(MediaWrapper.MEDIA_FORCE_AUDIO)
        PlaylistManager.playingAsAudio = true
        MediaUtils.openMedia(activity, media)
    }

    fun renameGroup(videoGroup: VideoGroup, newName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            videoGroup.rename(newName)
        }
    }

    fun renameFolder(videoGroup: FolderImpl, newName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val path = videoGroup.mMrl.toUri().retrieveParent()?.path
            val oldFolder = File(path, videoGroup.title)
            val newFolder = File(path, newName)
            oldFolder.renameTo(newFolder)
        }
    }

    fun removeFromGroup(medias: List<MediaWrapper>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            medias.forEach { media ->
                group?.remove(media.id)
            }
        }
    }

    fun removeFromGroup(media: MediaWrapper) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            group?.remove(media.id)
        }
    }

    fun ungroup(groups: List<MediaLibraryItem>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            groups.forEach { group ->
                if (group is VideoGroup) group.destroy()
            }
        }
    }

    fun ungroup(group: VideoGroup) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            group.destroy()
        }
    }

    suspend fun createGroup(medias: List<MediaWrapper>): VideoGroup? {
        if (medias.size < 2) return null
        return withContext(Dispatchers.IO) {
            val newGroup = medialibrary.createVideoGroup(medias.map { it.id }.toLongArray())
            if (newGroup.title.isNullOrBlank()) {
                newGroup.rename(medias[0].title)
                newGroup.title = medias[0].title
            }
            newGroup
        }
    }

    suspend fun groupSimilar(media: MediaWrapper) = withContext(Dispatchers.IO) {
        medialibrary.regroup(media.id)
    }

    suspend fun markAsPlayed(media: MediaLibraryItem) = withContext(Dispatchers.IO) {
        when (media) {
            is VideoGroup -> media.getAll().forEach {
                if (it.seen == 0L) it.setPlayCount(1L)
            }
            is Folder -> media.getAll().forEach {
                if (it.seen == 0L) it.setPlayCount(1L)
            }
            is MediaWrapper -> if (media.seen == 0L) media.setPlayCount(1L)
            else -> {}
        }
    }
    suspend fun markAsUnplayed(media: MediaLibraryItem) = withContext(Dispatchers.IO) {
        when (media) {
            is VideoGroup -> media.getAll().forEach {
                it.setPlayCount(0L)
            }
            is Folder -> media.getAll().forEach {
                it.setPlayCount(0L)
            }
            is MediaWrapper -> media.setPlayCount(0L)
            else -> {}
        }
    }
}

enum class VideoGroupingType {
    NONE, FOLDER, NAME
}

internal fun VideoGridFragment.getViewModel(type: VideoGroupingType = VideoGroupingType.NONE, folder: Folder?, group: VideoGroup?) = ViewModelProvider(requireActivity(), VideosViewModel.Factory(requireContext(), type, folder, group))[VideosViewModel::class.java]

internal fun CleanerListFragment.getViewModel(type: VideoGroupingType = VideoGroupingType.NONE, folder: Folder?, group: VideoGroup?) = ViewModelProvider(requireActivity(), VideosViewModel.Factory(requireContext(), type, folder, group))[VideosViewModel::class.java]

internal fun CleanerFragment.getViewModel(type: VideoGroupingType = VideoGroupingType.NONE, folder: Folder?, group: VideoGroup?) = ViewModelProvider(requireActivity(), VideosViewModel.Factory(requireContext(), type, folder, group))[VideosViewModel::class.java]

