/*****************************************************************************
 * VideoGridFragment.kt
 *
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
 */

package com.video.offline.videoplayer.gui.video

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.InitialPagedList
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.EventTools
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.medialibrary.media.FolderImpl
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.AppContextProvider
import org.videolan.resources.GROUP_VIDEOS_FOLDER
import org.videolan.resources.GROUP_VIDEOS_NAME
import org.videolan.resources.GROUP_VIDEOS_NONE
import org.videolan.resources.KEY_FOLDER
import org.videolan.resources.KEY_GROUP
import org.videolan.resources.KEY_GROUPING
import org.videolan.resources.KEY_GROUP_VIDEOS
import org.videolan.resources.KEY_MEDIA_LAST_PLAYLIST
import org.videolan.resources.KEY_VIDEOS_CARDS
import org.videolan.resources.KEY_VIDEOS_COMPACT
import org.videolan.resources.KEY_VIDEOS_LIST
import org.videolan.resources.MOVIEPEDIA_ACTIVITY
import org.videolan.resources.MOVIEPEDIA_MEDIA
import org.videolan.resources.PLAYLIST_TYPE_VIDEO
import org.videolan.resources.UPDATE_SEEN
import org.videolan.resources.util.parcelable
import org.videolan.resources.util.waitForML
import org.videolan.tools.KeyHelper
import org.videolan.tools.MultiSelectHelper
import org.videolan.tools.PLAYBACK_HISTORY
import org.videolan.tools.RESULT_RESTART
import org.videolan.tools.Settings
import org.videolan.tools.dp
import org.videolan.tools.isStarted
import org.videolan.tools.putSingle
import org.videolan.tools.retrieveParent
import org.videolan.tools.setGone
import org.videolan.tools.setVisible
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.VideoGridBinding
import com.video.offline.videoplayer.gui.HistoryAdapter
import com.video.offline.videoplayer.gui.MainActivity
import com.video.offline.videoplayer.gui.SecondaryActivity
import com.video.offline.videoplayer.gui.browser.MediaBrowserFragment
import com.video.offline.videoplayer.gui.dialogs.CURRENT_SORT
import com.video.offline.videoplayer.gui.dialogs.CtxActionReceiver
import com.video.offline.videoplayer.gui.dialogs.DISPLAY_IN_CARDS
import com.video.offline.videoplayer.gui.dialogs.DISPLAY_MODE
import com.video.offline.videoplayer.gui.dialogs.DisplaySettingsDialog
import com.video.offline.videoplayer.gui.dialogs.ONLY_FAVS
import com.video.offline.videoplayer.gui.dialogs.RenameDialog
import com.video.offline.videoplayer.gui.dialogs.SavePlaylistDialog
import com.video.offline.videoplayer.gui.dialogs.SettingSortsDialog
import com.video.offline.videoplayer.gui.dialogs.VIDEO_GROUPING
import com.video.offline.videoplayer.gui.dialogs.showContext
import com.video.offline.videoplayer.gui.helpers.AudioUtil.setRingtone
import com.video.offline.videoplayer.gui.helpers.Click
import com.video.offline.videoplayer.gui.helpers.ImageClick
import com.video.offline.videoplayer.gui.helpers.ItemOffsetDecoration
import com.video.offline.videoplayer.gui.helpers.LongClick
import com.video.offline.videoplayer.gui.helpers.MedialibraryUtils
import com.video.offline.videoplayer.gui.helpers.SimpleClick
import com.video.offline.videoplayer.gui.helpers.UiTools
import com.video.offline.videoplayer.gui.helpers.UiTools.addToGroup
import com.video.offline.videoplayer.gui.helpers.UiTools.addToPlaylist
import com.video.offline.videoplayer.gui.helpers.UiTools.createShortcut
import com.video.offline.videoplayer.gui.helpers.UiTools.showPinIfNeeded
import com.video.offline.videoplayer.gui.helpers.fillActionMode
import com.video.offline.videoplayer.gui.network.MRLPanelDialog
import com.video.offline.videoplayer.gui.network.TAG_
import com.video.offline.videoplayer.gui.view.EmptyLoadingState
import com.video.offline.videoplayer.interfaces.IHistory
import com.video.offline.videoplayer.interfaces.IRefreshable
import com.video.offline.videoplayer.media.MediaUtils
import com.video.offline.videoplayer.media.MediaUtils.appendMedia
import com.video.offline.videoplayer.media.PlaylistManager
import com.video.offline.videoplayer.media.getAll
import com.video.offline.videoplayer.providers.medialibrary.VideosProvider
import com.video.offline.videoplayer.reloadLibrary
import com.video.offline.videoplayer.util.ContextOption
import com.video.offline.videoplayer.util.ContextOption.CTX_ADD_GROUP
import com.video.offline.videoplayer.util.ContextOption.CTX_ADD_SHORTCUT
import com.video.offline.videoplayer.util.ContextOption.CTX_ADD_TO_PLAYLIST
import com.video.offline.videoplayer.util.ContextOption.CTX_APPEND
import com.video.offline.videoplayer.util.ContextOption.CTX_BAN_FOLDER
import com.video.offline.videoplayer.util.ContextOption.CTX_DELETE
import com.video.offline.videoplayer.util.ContextOption.CTX_DOWNLOAD_SUBTITLES
import com.video.offline.videoplayer.util.ContextOption.CTX_FAV_ADD
import com.video.offline.videoplayer.util.ContextOption.CTX_FAV_REMOVE
import com.video.offline.videoplayer.util.ContextOption.CTX_FIND_METADATA
import com.video.offline.videoplayer.util.ContextOption.CTX_GO_TO_FOLDER
import com.video.offline.videoplayer.util.ContextOption.CTX_GROUP_SIMILAR
import com.video.offline.videoplayer.util.ContextOption.CTX_INFORMATION
import com.video.offline.videoplayer.util.ContextOption.CTX_MARK_ALL_AS_PLAYED
import com.video.offline.videoplayer.util.ContextOption.CTX_MARK_ALL_AS_UNPLAYED
import com.video.offline.videoplayer.util.ContextOption.CTX_MARK_AS_PLAYED
import com.video.offline.videoplayer.util.ContextOption.CTX_MARK_AS_UNPLAYED
import com.video.offline.videoplayer.util.ContextOption.CTX_PLAY
import com.video.offline.videoplayer.util.ContextOption.CTX_PLAY_ALL
import com.video.offline.videoplayer.util.ContextOption.CTX_PLAY_AS_AUDIO
import com.video.offline.videoplayer.util.ContextOption.CTX_PLAY_FROM_START
import com.video.offline.videoplayer.util.ContextOption.CTX_PLAY_NEXT
import com.video.offline.videoplayer.util.ContextOption.CTX_PRIVATE
import com.video.offline.videoplayer.util.ContextOption.CTX_REMOVE_GROUP
import com.video.offline.videoplayer.util.ContextOption.CTX_RENAME
import com.video.offline.videoplayer.util.ContextOption.CTX_RENAME_GROUP
import com.video.offline.videoplayer.util.ContextOption.CTX_SET_RINGTONE
import com.video.offline.videoplayer.util.ContextOption.CTX_SHARE
import com.video.offline.videoplayer.util.ContextOption.CTX_UNGROUP
import com.video.offline.videoplayer.util.ContextOption.Companion.createCtxFolderFlags
import com.video.offline.videoplayer.util.ContextOption.Companion.createCtxVideoFlags
import com.video.offline.videoplayer.util.ContextOption.Companion.createCtxVideoGroupFlags
import com.video.offline.videoplayer.util.Permissions
import com.video.offline.videoplayer.util.isMissing
import com.video.offline.videoplayer.util.isTalkbackIsEnabled
import com.video.offline.videoplayer.util.launchWhenStarted
import com.video.offline.videoplayer.util.onAnyChange
import com.video.offline.videoplayer.util.share
import com.video.offline.videoplayer.util.showParentFolder
import com.video.offline.videoplayer.viewmodels.DisplaySettingsViewModel
import com.video.offline.videoplayer.viewmodels.HistoryModel
import com.video.offline.videoplayer.viewmodels.mobile.VideoGroupingType
import com.video.offline.videoplayer.viewmodels.mobile.VideosViewModel
import com.video.offline.videoplayer.viewmodels.mobile.getViewModel

private const val TAG = "VLC/VideoListFragment"

private const val KEY_SELECTION = "key_selection"

class VideoGridFragment : MediaBrowserFragment<VideosViewModel>(),
    SwipeRefreshLayout.OnRefreshListener, CtxActionReceiver, IRefreshable, IHistory, View.OnClickListener {

    private lateinit var dataObserver: RecyclerView.AdapterDataObserver
    private lateinit var videoListAdapter: VideoListAdapter
    private lateinit var multiSelectHelper: MultiSelectHelper<MediaLibraryItem>
    private lateinit var multiSelectHelperHistory: MultiSelectHelper<MediaWrapper>
    private lateinit var binding: VideoGridBinding
    private var gridItemDecoration: RecyclerView.ItemDecoration? = null
    private lateinit var settings: SharedPreferences
    private val historyAdapter: HistoryAdapter = HistoryAdapter(true)
    private var savedSelection = ArrayList<Int>()

    @Suppress("UNCHECKED_CAST")
    private fun getMultiHelperHistory(): MultiSelectHelper<HistoryModel>? = historyAdapter.multiSelectHelper as? MultiSelectHelper<HistoryModel>

    private lateinit var historyViewModel: HistoryModel

    //in case of fragment being hosted by other fragments, it's useful to prevent the
    //FAB visibility to be locked hidden
    override val isMainNavigationPoint = false
    override val hasTabs: Boolean
        get() = parentFragment != null

    private val displaySettingsViewModel: DisplaySettingsViewModel by activityViewModels()
    private fun FragmentActivity.open(item: MediaLibraryItem) {
        val i = Intent(activity, SecondaryActivity::class.java)
        i.putExtra(SecondaryActivity.KEY_FRAGMENT, SecondaryActivity.VIDEO_GROUP_LIST)
        if (item is Folder) i.putExtra(KEY_FOLDER, item)
        else if (item is VideoGroup) i.putExtra(KEY_GROUP, item)
        startActivityForResult(i, SecondaryActivity.ACTIVITY_RESULT_SECONDARY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (savedInstanceState?.getIntegerArrayList(KEY_SELECTION))?.let { savedSelection = it }
        if (!::settings.isInitialized) settings = Settings.getInstance(requireContext())
        if (!::videoListAdapter.isInitialized) {
            val seenMarkVisible = settings.getBoolean("media_seen", true)
            videoListAdapter = VideoListAdapter(seenMarkVisible, !Settings.getInstance(requireActivity()).getBoolean(PLAYBACK_HISTORY, true)).apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }
            dataObserver = videoListAdapter.onAnyChange {
                updateEmptyView()
                if (::binding.isInitialized) binding.fastScroller.setRecyclerView(binding.videoGrid, viewModel.provider)
            }
            multiSelectHelper = videoListAdapter.multiSelectHelper
            val folder = if (savedInstanceState != null) savedInstanceState.parcelable<Folder>(KEY_FOLDER)
            else arguments?.parcelable(KEY_FOLDER)
            val parentGroup = if (savedInstanceState != null) savedInstanceState.parcelable<VideoGroup>(KEY_GROUP)
                    else arguments?.parcelable(KEY_GROUP)
            val grouping = if (parentGroup != null || folder != null) VideoGroupingType.NONE else when (Settings.getInstance(requireContext()).getString(KEY_GROUP_VIDEOS, null) ?: GROUP_VIDEOS_NAME) {
                GROUP_VIDEOS_NONE -> VideoGroupingType.NONE
                GROUP_VIDEOS_FOLDER -> VideoGroupingType.FOLDER
                else -> VideoGroupingType.NAME
            }
            viewModel = getViewModel(grouping, folder, parentGroup)
            historyViewModel = ViewModelProvider(requireActivity(), HistoryModel.Factory(requireContext()))[HistoryModel::class.java]
            setDataObservers()
            EventTools.getInstance().lastThumb.observe(this, thumbObs)
            videoListAdapter.events.onEach { it.process() }.launchWhenStarted(lifecycleScope)
            historyAdapter.events.onEach { it.process() }.launchWhenStarted(lifecycleScope)
        }
    }

    override fun refresh() = historyViewModel.refresh()

    override fun isEmpty(): Boolean {
        return historyAdapter.isEmpty()
    }

    override fun clearHistory() {
        historyViewModel.clearHistory()
        Medialibrary.getInstance().clearHistory(Medialibrary.HISTORY_TYPE_GLOBAL)
    }

    private fun setDataObservers() {
        videoListAdapter.dataType = viewModel.groupingType
        viewModel.provider.loading.observe(this@VideoGridFragment) { loading ->
            if (isResumed) setRefreshing(loading) { refresh ->
                if (!refresh) {
                    menu?.let { UiTools.updateSortTitles(it, viewModel.provider) }
                    restoreMultiSelectHelperHistory()
                }
            }
        }
        videoListAdapter.showFilename.set(viewModel.groupingType == VideoGroupingType.NONE && viewModel.provider.sort == Medialibrary.SORT_FILENAME)
        lifecycleScope.launch {
            waitForML()
            viewModel.provider.pagedList.observe(this@VideoGridFragment) {
                @Suppress("UNCHECKED_CAST")
                (it as? PagedList<MediaLibraryItem>)?.let { pagedList -> videoListAdapter.submitList(pagedList) }
                updateEmptyView()
                restoreMultiSelectHelper()
                if (it !is InitialPagedList<*, *> && activity?.isFinishing == false && viewModel.group != null && it.size < 2 && viewModel.filterQuery.isNullOrEmpty()) requireActivity().finish()
                setFabPlayVisibility(true)
            }
            historyViewModel.dataset.observe(this@VideoGridFragment) { list ->
                list?.let {
                    historyAdapter.update(it)
                    if (list.isEmpty()) binding.historyEntry.setGone() else {
                        binding.historyEntry.setVisible()
                        binding.historyEntry.loading.state = EmptyLoadingState.NONE
                    }
                    if (list.isNotEmpty()) binding.historyEntry.actionButton.setVisible() else binding.historyEntry.actionButton.setGone()
                }
                restoreMultiSelectHelper()
            }
            historyViewModel.loading.observe(this@VideoGridFragment) {
                lifecycleScope.launchWhenStarted {
                    if (it) delay(300L)
                    (activity as? MainActivity)?.refreshing = it
                    if (it) binding.historyEntry.loading.state = EmptyLoadingState.LOADING
                }
            }
        }
        EventTools.getInstance().lastThumb.observe(this) {
            videoListAdapter.updateThumb(it)
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.ml_menu_last_playlist).isVisible =
            settings.contains(KEY_MEDIA_LAST_PLAYLIST)
        menu.findItem(R.id.rename_group).isVisible = viewModel.group != null
        menu.findItem(R.id.ungroup).isVisible = viewModel.group != null
        menu.findItem(R.id.ml_menu_sortby).isVisible = false
        // TODO: VISIBILITY
//        menu.findItem(R.id.ml_menu_display_options).isVisible = parentFragment is VideoBrowserFragment
        menu.findItem(R.id.ml_menu_display_options).isVisible = false
        if (requireActivity().isTalkbackIsEnabled()) menu.findItem(R.id.play_all).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ml_menu_select -> {
                onLongClick(0)
            }
            R.id.ml_menu_network_stream -> {
                MRLPanelDialog().show(requireActivity().supportFragmentManager, TAG_)
            }
            R.id.ml_menu_last_playlist -> {
                MediaUtils.loadlastPlaylist(activity, PLAYLIST_TYPE_VIDEO)
            }

            R.id.rename_group -> {
                viewModel.group?.let { renameGroup(it) }
            }

            R.id.ungroup -> {
                viewModel.group?.let {
                    lifecycleScope.launch {
                        if (requireActivity().showPinIfNeeded()) viewModel.ungroup(
                            it
                        )
                    }
                }
            }

            R.id.play_all -> {
                onFabPlayClick(binding.root)
            }

            R.id.ml_menu_display_options -> {
                //filter all sorts and keep only applicable ones
                val sorts = arrayListOf(
                    Medialibrary.SORT_ALPHA,
                    Medialibrary.SORT_FILENAME,
                    Medialibrary.SORT_ARTIST,
                    Medialibrary.SORT_ALBUM,
                    Medialibrary.SORT_DURATION,
                    Medialibrary.SORT_RELEASEDATE,
                    Medialibrary.SORT_LASTMODIFICATIONDATE,
                    Medialibrary.SORT_FILESIZE,
                    Medialibrary.NbMedia,
                    Medialibrary.SORT_INSERTIONDATE
                ).filter {
                    viewModel.provider.canSortBy(it)
                }
                //Open the display settings Bottom sheet
                DisplaySettingsDialog.newInstance(
                    displayInCards = settings.getBoolean(KEY_VIDEOS_CARDS, true),
                    onlyFavs = viewModel.provider.onlyFavorites,
                    sorts = sorts,
                    currentSort = viewModel.provider.sort,
                    currentSortDesc = viewModel.provider.desc,
                    videoGroup = settings.getString(KEY_GROUP_VIDEOS, GROUP_VIDEOS_NAME)
                ).show(requireActivity().supportFragmentManager, "DisplaySettingsDialog")
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    override fun sortBy(sort: Int) {
        videoListAdapter.showFilename.set(sort == Medialibrary.SORT_FILENAME)
        super.sortBy(sort)
    }

    private fun changeGroupingType(type: VideoGroupingType) {
        viewModel.provider.pagedList.removeObservers(this)
        viewModel.provider.loading.removeObservers(this)
        viewModel.changeGroupingType(type)
        setDataObservers()
        (activity as? AppCompatActivity)?.run {
            supportActionBar?.title = title
            invalidateOptionsMenu()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = VideoGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val empty = viewModel.isEmpty()
        binding.emptyLoading.state =
            if (empty) EmptyLoadingState.LOADING else EmptyLoadingState.NONE
        binding.empty = empty
        binding.emptyLoading.setOnNoMediaClickListener {
            requireActivity().setResult(RESULT_RESTART)
        }
        swipeRefreshLayout.setOnRefreshListener(this)
        binding.videoGrid.adapter = videoListAdapter
        binding.fastScroller.attachToCoordinator(
            requireActivity().findViewById<View>(R.id.appbar) as AppBarLayout,
            requireActivity().findViewById<View>(R.id.coordinator) as CoordinatorLayout,
            requireActivity().findViewById<View>(R.id.fab) as FloatingActionButton
        )
        binding.fastScroller.setRecyclerView(binding.videoGrid, viewModel.provider)

        (parentFragment as? VideoBrowserFragment)?.videoGridOnlyFavorites =
            viewModel.provider.onlyFavorites

        binding.historyEntry.list.adapter = historyAdapter
        binding.historyEntry.list.nextFocusUpId = R.id.ml_menu_search
        binding.historyEntry.list.nextFocusLeftId = android.R.id.list
        binding.historyEntry.list.nextFocusRightId = android.R.id.list
        binding.historyEntry.list.nextFocusForwardId = android.R.id.list

        binding.historyEntry.setOnActionClickListener {
            val i = Intent(requireActivity(), SecondaryActivity::class.java)
            i.putExtra("fragment", SecondaryActivity.HISTORY)
            requireActivity().startActivityForResult(i, SecondaryActivity.ACTIVITY_RESULT_SECONDARY)
        }

        multiSelectHelperHistory = historyAdapter.multiSelectHelper
        binding.historyEntry.list.requestFocus()
        registerForContextMenu(binding.historyEntry.list)
    }

    override fun onDisplaySettingChanged(key: String, value: Any) {
        when (key) {
            DISPLAY_MODE -> {
                settings.putSingle(DISPLAY_MODE, value as String)
                updateViewMode()
            }

            DISPLAY_IN_CARDS -> {
                settings.putSingle(KEY_VIDEOS_CARDS, value as Boolean)
                updateViewMode()
            }

            ONLY_FAVS -> {
                viewModel.provider.showOnlyFavs(value as Boolean)
                viewModel.refresh()
                (parentFragment as? VideoBrowserFragment)?.videoGridOnlyFavorites = value
            }

            CURRENT_SORT -> {
                @Suppress("UNCHECKED_CAST") val sort = value as Pair<Int, Boolean>
                viewModel.provider.sort = sort.first
                viewModel.provider.desc = sort.second
                viewModel.provider.saveSort()
                viewModel.refresh()
            }

            VIDEO_GROUPING -> {
                val videoGroup = value as DisplaySettingsDialog.VideoGroup
                settings.putSingle(KEY_GROUP_VIDEOS, videoGroup.value)
                changeGroupingType(videoGroup.type)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateEmptyView()
    }

    override fun onStart() {
        super.onStart()
        registerForContextMenu(binding.videoGrid)
        updateViewMode()
        setFabPlayVisibility(true)
        fabPlay?.setImageResource(R.drawable.ic_fab_play)
        fabPlay?.contentDescription = getString(R.string.play)
        if (!viewModel.isEmpty() && getFilterQuery() == null) viewModel.refresh()
        historyViewModel.refresh()
    }

    override fun onStop() {
        super.onStop()
        unregisterForContextMenu(binding.videoGrid)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getMultiHelperHistory()?.let {
            outState.putIntegerArrayList(KEY_SELECTION, it.selectionMap)
        }

        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_FOLDER, viewModel.folder)
        outState.putParcelable(KEY_GROUP, viewModel.group)
        outState.putSerializable(KEY_GROUPING, viewModel.groupingType)
    }

    private fun restoreMultiSelectHelperHistory() {
        getMultiHelperHistory()?.let {
            if (savedSelection.size > 0) {
                var hasOneSelected = false
                for (i in 0 until savedSelection.size) {

                    it.selectionMap.addAll(savedSelection)
                    hasOneSelected = savedSelection.isNotEmpty()
                }
                if (hasOneSelected) startActionMode()
                savedSelection.clear()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gridItemDecoration = null
        swipeRefreshLayout.setOnRefreshListener(null)
        if (::dataObserver.isInitialized) videoListAdapter.unregisterAdapterDataObserver(
            dataObserver
        )
    }

    override fun getTitle() = when (viewModel.groupingType) {
        VideoGroupingType.NONE -> viewModel.folder?.displayTitle ?: viewModel.group?.displayTitle
        ?: getString(R.string.videos)

        VideoGroupingType.FOLDER -> getString(R.string.videos_folders_title)
        VideoGroupingType.NAME -> getString(R.string.videos_groups_title)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getMultiHelper(): MultiSelectHelper<VideosViewModel>? =
        if (::videoListAdapter.isInitialized) videoListAdapter.multiSelectHelper as? MultiSelectHelper<VideosViewModel> else null

    private fun updateViewMode() {
        if (view == null || activity == null) {
            Log.w(TAG, "Unable to setup the view")
            return
        }
        val res = resources
        if (gridItemDecoration == null) gridItemDecoration = ItemOffsetDecoration(
            resources, R.dimen.left_right_1610_margin, R.dimen.top_bottom_1610_margin
        )
        val displayMode: String? = settings.getString(DISPLAY_MODE, KEY_VIDEOS_LIST)
        // Select between grid or list
        binding.videoGrid.removeItemDecoration(gridItemDecoration!!)

        displayMode?.also {
            Log.d(TAG, "updateViewMode: $it")
            when (it) {
                KEY_VIDEOS_CARDS -> {
                    binding.videoGrid.setNumColumns(3)
                    val thumbnailWidth = res.getDimensionPixelSize(R.dimen.grid_card_thumb_width)
                    val margin = binding.videoGrid.paddingStart + binding.videoGrid.paddingEnd
                    val columnWidth = binding.videoGrid.getPerfectColumnWidth(
                        thumbnailWidth, margin
                    ) - res.getDimensionPixelSize(R.dimen.left_right_1610_margin) * 3
                    binding.videoGrid.columnWidth = columnWidth
                    binding.videoGrid.addItemDecoration(gridItemDecoration!!)
                    binding.videoGrid.setPadding(4.dp, 4.dp, 4.dp, 4.dp)
                    binding.viewModeIv.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(), R.drawable.view_mode_grid
                        )
                    )
                }

                KEY_VIDEOS_LIST -> {
                    binding.videoGrid.setNumColumns(1)
                    binding.videoGrid.setPadding(0, 0, 0, 0)
                    binding.viewModeIv.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(), R.drawable.view_mode_list
                        )
                    )
                }

                else -> {
                    binding.videoGrid.setNumColumns(1)
                    binding.videoGrid.setPadding(0, 0, 0, 0)
                    binding.viewModeIv.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(), R.drawable.view_mode_compact
                        )
                    )
                }
            }

            videoListAdapter.displayMode = it
        }
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick: $v")
        when (v) {
            binding.sortIv -> {
                Log.d(TAG, "onClick: sort")
                //Open the display settings Bottom sheet
                SettingSortsDialog.newInstance(
                    currentSort = viewModel.provider.sort,
                    currentSortDesc = viewModel.provider.desc,
                    videoGroup = settings.getString(KEY_GROUP_VIDEOS, GROUP_VIDEOS_NAME)?: GROUP_VIDEOS_NAME
                ).show(requireActivity().supportFragmentManager, "DisplaySettingsDialog")
            }

            binding.viewModeIv -> {
                Log.d(TAG, "onClick: viewMode")
                val currentDisplayMode = settings.getString(DISPLAY_MODE, KEY_VIDEOS_LIST)
                when (currentDisplayMode) {
                    KEY_VIDEOS_LIST -> {
                        lifecycleScope.launch {
                            displaySettingsViewModel.send(
                                DISPLAY_MODE, KEY_VIDEOS_CARDS
                            )
                        }
                    }

                    KEY_VIDEOS_CARDS -> {
                        lifecycleScope.launch {
                            displaySettingsViewModel.send(
                                DISPLAY_MODE, KEY_VIDEOS_COMPACT
                            )
                        }
                    }

                    KEY_VIDEOS_COMPACT -> {
                        lifecycleScope.launch {
                            displaySettingsViewModel.send(
                                DISPLAY_MODE, KEY_VIDEOS_LIST
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onFabPlayClick(view: View) {
        viewModel.playAll(activity)
    }

    private fun updateEmptyView() {
        if (!::binding.isInitialized) return
        if (!isAdded) return
        val empty = viewModel.isEmpty() && videoListAdapter.currentList.isNullOrEmpty()
        val working = viewModel.provider.loading.value != false
        binding.emptyLoading.emptyText =
            viewModel.filterQuery?.let { getString(R.string.empty_search, it) }
                ?: if (viewModel.provider.onlyFavorites) getString(R.string.nofav) else getString(R.string.nomedia)
        binding.emptyLoading.state = when {
            !Permissions.canReadStorage(AppContextProvider.appContext) && empty -> EmptyLoadingState.MISSING_PERMISSION
            empty && working -> EmptyLoadingState.LOADING
            empty && !working && viewModel.provider.onlyFavorites -> EmptyLoadingState.EMPTY_FAVORITES
            empty && !working && viewModel.filterQuery == null -> EmptyLoadingState.EMPTY
            empty && !working && viewModel.filterQuery != null -> EmptyLoadingState.EMPTY_SEARCH
            else -> EmptyLoadingState.NONE
        }
        binding.empty = empty && !working
        when (viewModel.groupingType) {
            VideoGroupingType.NAME, VideoGroupingType.NONE -> {
                binding.videoCountTextView.text = buildString {
                    append(videoListAdapter.all.size)
                    append(" Videos")
                }
                binding.sortIv.setOnClickListener(this)
                binding.viewModeIv.setOnClickListener(this)
            }

            VideoGroupingType.FOLDER -> {
                binding.videoCountTextView.text = buildString {
                    append(videoListAdapter.all.size)
                    append(" Folders")
                }
                binding.sortIv.setOnClickListener(this)
                binding.viewModeIv.setOnClickListener(this)
            }


        }
    }

    override fun onRefresh() {
        activity?.reloadLibrary()
    }

    override fun setFabPlayVisibility(enable: Boolean) {
        super.setFabPlayVisibility(!viewModel.isEmpty() && enable)
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        when (viewModel.groupingType) {
            VideoGroupingType.NONE -> mode.menuInflater.inflate(R.menu.action_mode_video, menu)
            VideoGroupingType.FOLDER -> mode.menuInflater.inflate(R.menu.action_mode_folder, menu)
            VideoGroupingType.NAME -> mode.menuInflater.inflate(
                R.menu.action_mode_video_group, menu
            )
        }
        multiSelectHelper.toggleActionMode(true, videoListAdapter.itemCount)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val count = multiSelectHelper.getSelectionCount()
        if (count == 0) {
            stopActionMode()
            return false
        }
        lifecycleScope.launch { fillActionMode(requireActivity(), mode, multiSelectHelper) }
        when (viewModel.groupingType) {
            VideoGroupingType.NONE -> {
                menu.findItem(R.id.action_video_append).isVisible = PlaylistManager.hasMedia()
                menu.findItem(R.id.action_video_info).isVisible = count == 1
                menu.findItem(R.id.action_remove_from_group).isVisible = viewModel.group != null
                menu.findItem(R.id.action_add_to_group).isVisible =
                    viewModel.group != null && count > 1
                menu.findItem(R.id.action_mode_go_to_folder).isVisible = checkFolderToParent(count)
            }

            VideoGroupingType.NAME -> {
                menu.findItem(R.id.action_ungroup).isVisible =
                    !multiSelectHelper.getSelection().any { it !is VideoGroup }
                menu.findItem(R.id.action_rename).isVisible =
                    count == 1 && !multiSelectHelper.getSelection().any { it !is VideoGroup }
                menu.findItem(R.id.action_group_similar).isVisible =
                    count == 1 && multiSelectHelper.getSelection().filterIsInstance<VideoGroup>()
                        .isEmpty()
                menu.findItem(R.id.action_mode_go_to_folder).isVisible = checkFolderToParent(count)
            }

            else -> {}
        }
        menu.findItem(R.id.action_mode_favorite_add).isVisible =
            multiSelectHelper.getSelection().none { it.isFavorite }
        menu.findItem(R.id.action_mode_favorite_remove).isVisible =
            multiSelectHelper.getSelection().none { !it.isFavorite }
        return true
    }

    private fun checkFolderToParent(count: Int) = if (count == 1) {
        (multiSelectHelper.getSelection().firstOrNull() as? MediaWrapper)?.let {
            if (it.type != MediaWrapper.TYPE_VIDEO) return@let false
            return@let it.uri.retrieveParent() != null
        } ?: false
    } else false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if (!isStarted()) return false
        when (viewModel.groupingType) {
            VideoGroupingType.NONE -> {
                val list = multiSelectHelper.getSelection().map { it as MediaWrapper }
                if (list.isNotEmpty()) {
                    when (item.itemId) {
                        R.id.action_video_play -> MediaUtils.openList(activity, list, 0)
                        R.id.action_video_append -> appendMedia(activity, list)
                        R.id.action_video_share -> requireActivity().share(list)
                        R.id.action_video_info -> showInfoDialog(list.first())
                        R.id.action_video_download_subtitles -> MediaUtils.getSubs(
                            requireActivity(), list
                        )

                        R.id.action_video_play_audio -> {
                            for (media in list) media.addFlags(MediaWrapper.MEDIA_FORCE_AUDIO)
                            MediaUtils.openList(activity, list, 0)
                        }

                        R.id.action_mode_audio_add_playlist -> requireActivity().addToPlaylist(list)
                        R.id.action_video_delete -> removeItems(list)
                        R.id.action_remove_from_group -> viewModel.removeFromGroup(list)
                        R.id.action_ungroup -> viewModel.ungroup(list)
                        R.id.action_add_to_group -> addToGroup(list)
                        R.id.action_mode_go_to_folder -> (list.first() as? MediaWrapper)?.let {
                            showParentFolder(
                                it
                            )
                        }

                        R.id.action_mode_favorite_add -> lifecycleScope.launch {
                            viewModel.changeFavorite(
                                list, true
                            )
                        }

                        R.id.action_mode_favorite_remove -> lifecycleScope.launch {
                            viewModel.changeFavorite(
                                list, false
                            )
                        }

                        else -> {
                            stopActionMode()
                            return false
                        }
                    }
                }
            }

            VideoGroupingType.FOLDER -> {
                val selection = ArrayList<Folder>()
                for (mediaLibraryItem in multiSelectHelper.getSelection()) {
                    selection.add(mediaLibraryItem as FolderImpl)
                }
                when (item.itemId) {
                    R.id.action_folder_play -> viewModel.playFoldersSelection(selection)
                    R.id.action_folder_append -> viewModel.appendFoldersSelection(selection)
                    R.id.action_folder_add_playlist -> lifecycleScope.launch {
                        requireActivity().addToPlaylist(withContext(Dispatchers.Default) { selection.getAll() })
                    }

                    R.id.action_video_delete -> removeItems(selection.getAll())
                    R.id.action_mode_favorite_add -> lifecycleScope.launch {
                        viewModel.changeFavorite(
                            selection.getAll(), true
                        )
                    }

                    R.id.action_mode_favorite_remove -> lifecycleScope.launch {
                        viewModel.changeFavorite(
                            selection.getAll(), false
                        )
                    }

                    else -> return false
                }
            }

            VideoGroupingType.NAME -> {
                val selection = multiSelectHelper.getSelection()
                when (item.itemId) {
                    R.id.action_videogroup_play -> MediaUtils.openList(
                        activity, selection.getAll(), 0
                    )

                    R.id.action_videogroup_append -> appendMedia(
                        activity, selection.getAll()
                    )

                    R.id.action_videogroup_add_playlist -> lifecycleScope.launch {
                        requireActivity().addToPlaylist(withContext(Dispatchers.Default) { selection.getAll() })
                    }

                    R.id.action_group_similar -> lifecycleScope.launch {
                        viewModel.groupSimilar(
                            selection.getAll().first()
                        )
                    }

                    R.id.action_ungroup -> viewModel.ungroup(selection.first() as VideoGroup)
                    R.id.action_rename -> renameGroup(selection.first() as VideoGroup)
                    R.id.action_add_to_group -> addToGroup(selection)
                    R.id.action_mode_go_to_folder -> (selection.first() as? MediaWrapper)?.let {
                        showParentFolder(
                            it
                        )
                    }

                    R.id.action_video_delete -> removeItems(selection.getAll())
                    R.id.action_mode_favorite_add -> lifecycleScope.launch {
                        viewModel.changeFavorite(
                            selection.getAll(), true
                        )
                    }

                    R.id.action_mode_favorite_remove -> lifecycleScope.launch {
                        viewModel.changeFavorite(
                            selection.getAll(), false
                        )
                    }

                    else -> return false
                }
            }
        }
        stopActionMode()
        return true
    }

    private fun addToGroup(selection: List<MediaLibraryItem>) {
        requireActivity().addToGroup(selection.getAll(), selection.size == 1) {
            lifecycleScope.launch {
                viewModel.createGroup(selection.getAll())?.let {
                    // we already are in a group. Finishing to avoid stacking multiple group activities
                    if (viewModel.groupingType == VideoGroupingType.NONE) requireActivity().finish()
                    activity?.open(it)
                }
            }
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        setFabPlayVisibility(true)
        multiSelectHelper.clearSelection()
        multiSelectHelper.toggleActionMode(false, videoListAdapter.itemCount)
    }

    fun updateSeenMediaMarker() {
        videoListAdapter.setSeenMediaMarkerVisible(settings.getBoolean("media_seen", true))
        videoListAdapter.notifyItemRangeChanged(0, videoListAdapter.itemCount - 1, UPDATE_SEEN)
    }

    override fun onCtxAction(position: Int, option: ContextOption) {
        if (position >= videoListAdapter.itemCount) return
        val activity = activity ?: return
        when (val media = videoListAdapter.getItem(position)) {
            is MediaWrapper -> when (option) {
                CTX_PLAY_FROM_START -> viewModel.playVideo(
                    activity, media, position, fromStart = true
                )

                CTX_PLAY_AS_AUDIO -> viewModel.playAudio(activity, media)
                CTX_PLAY_ALL -> viewModel.playVideo(activity, media, position, forceAll = true)
                CTX_PLAY -> viewModel.play(position)
                CTX_INFORMATION -> showInfoDialog(media)
                CTX_DELETE -> removeItem(media)
                CTX_PRIVATE -> makePrivateItem(media)
                CTX_APPEND -> appendMedia(activity, media)
                CTX_SET_RINGTONE -> requireActivity().setRingtone(media)
                CTX_PLAY_NEXT -> MediaUtils.insertNext(requireActivity(), media.tracks)
                CTX_DOWNLOAD_SUBTITLES -> MediaUtils.getSubs(requireActivity(), media)
                CTX_ADD_TO_PLAYLIST -> requireActivity().addToPlaylist(
                    media.tracks, SavePlaylistDialog.KEY_NEW_TRACKS
                )

                CTX_FIND_METADATA -> {
                    val intent = Intent().apply {
                        setClassName(requireContext().applicationContext, MOVIEPEDIA_ACTIVITY)
                        apply { putExtra(MOVIEPEDIA_MEDIA, media) }
                    }
                    startActivity(intent)
                }

                CTX_SHARE -> lifecycleScope.launch {
                    (requireActivity() as AppCompatActivity).share(
                        media
                    )
                }

                CTX_REMOVE_GROUP -> viewModel.removeFromGroup(media)
                CTX_ADD_GROUP -> requireActivity().addToGroup(listOf(media), true) {}
                CTX_GROUP_SIMILAR -> lifecycleScope.launch {
                    if (!requireActivity().showPinIfNeeded()) viewModel.groupSimilar(
                        media
                    )
                }

                CTX_MARK_AS_PLAYED -> lifecycleScope.launch { viewModel.markAsPlayed(media) }
                CTX_MARK_AS_UNPLAYED -> lifecycleScope.launch { viewModel.markAsUnplayed(media) }
                CTX_FAV_ADD, CTX_FAV_REMOVE -> lifecycleScope.launch(Dispatchers.IO) {
                    media.isFavorite = option == CTX_FAV_ADD
                }

                CTX_GO_TO_FOLDER -> showParentFolder(media)
                CTX_ADD_SHORTCUT -> lifecycleScope.launch { requireActivity().createShortcut(media) }
                else -> {}
            }

            is Folder -> when (option) {
                CTX_PLAY -> viewModel.play(position)
                CTX_APPEND -> viewModel.append(position)
                CTX_PLAY_AS_AUDIO -> viewModel.playFoldersSelectionBackground(listOf(media))
                CTX_RENAME -> {
                    renameFolder(media)
                }
                CTX_DELETE -> removeItems(media.getAll())
                CTX_PRIVATE -> makePrivateItem(media)
                CTX_ADD_TO_PLAYLIST -> viewModel.addItemToPlaylist(requireActivity(), position)
                CTX_MARK_ALL_AS_PLAYED -> lifecycleScope.launch { viewModel.markAsPlayed(media) }
                CTX_MARK_ALL_AS_UNPLAYED -> lifecycleScope.launch { viewModel.markAsUnplayed(media) }
                CTX_FAV_ADD, CTX_FAV_REMOVE -> lifecycleScope.launch(Dispatchers.IO) {
                    media.isFavorite = option == CTX_FAV_ADD
                }

                CTX_BAN_FOLDER -> banFolder(media)
                else -> {}
            }

            is VideoGroup -> when (option) {
                CTX_PLAY_ALL -> viewModel.play(position)
                CTX_PLAY -> viewModel.play(position)
                CTX_APPEND -> viewModel.append(position)
                CTX_ADD_TO_PLAYLIST -> viewModel.addItemToPlaylist(requireActivity(), position)
                CTX_RENAME_GROUP -> renameGroup(media)
                CTX_UNGROUP -> lifecycleScope.launch {
                    if (!requireActivity().showPinIfNeeded()) viewModel.ungroup(
                        media
                    )
                }

                CTX_MARK_ALL_AS_PLAYED -> lifecycleScope.launch { viewModel.markAsPlayed(media) }
                CTX_MARK_ALL_AS_UNPLAYED -> lifecycleScope.launch { viewModel.markAsUnplayed(media) }
                CTX_ADD_GROUP -> requireActivity().addToGroup(listOf(media).getAll(), true) {}
                CTX_FAV_ADD, CTX_FAV_REMOVE -> lifecycleScope.launch(Dispatchers.IO) {
                    media.isFavorite = option == CTX_FAV_ADD
                }

                else -> {}
            }
        }
    }

    private fun banFolder(folder: Folder) {
        folder.mMrl.toUri().path?.let { path ->
            lifecycleScope.launch(Dispatchers.IO) {
                val roots: Array<String> = Medialibrary.getInstance().foldersList
                val strippedPath = path.removePrefix("file://")
                for (root in roots) {
                    if (root.removePrefix("file://") == strippedPath) {
                        Log.w(TAG, "banFolder: trying to ban root: $root")
                        lifecycleScope.launch(Dispatchers.Main) {
                            UiTools.snacker(requireActivity(), getString(R.string.cant_ban_root))
                        }
                        return@launch
                    }
                }
                MedialibraryUtils.banDir(strippedPath)
            }

        } ?: Log.e(TAG, "banFolder: path is null")
    }

    private fun renameFolder(media: MediaLibraryItem) {
        val dialog = RenameDialog.newInstance(media)
        dialog.setListener { item, name ->
            viewModel.renameFolder(item as FolderImpl, name)
            onRefresh()
        }
        dialog.show(requireActivity().supportFragmentManager, RenameDialog::class.simpleName)
    }

    private fun renameGroup(media: VideoGroup) {
        val dialog = RenameDialog.newInstance(media)
        dialog.setListener { item, name ->
            viewModel.renameGroup(item as VideoGroup, name)
            (activity as? AppCompatActivity)?.run {
                supportActionBar?.title = name
            }
        }
        dialog.show(requireActivity().supportFragmentManager, RenameDialog::class.simpleName)
    }

    private val thumbObs = Observer<MediaWrapper> { media ->
        if (!::videoListAdapter.isInitialized || viewModel.provider !is VideosProvider) return@Observer
        val position = viewModel.provider.pagedList.value?.indexOf(media) ?: return@Observer
        val item = videoListAdapter.getItem(position) as? MediaWrapper
        item?.run {
            artworkURL = media.artworkURL
            videoListAdapter.notifyItemChanged(position)
        }
    }

    private fun VideoAction.process() {
        when (this) {
            is VideoClick -> {
                onClick(position, item)
            }

            is VideoLongClick -> {
                if ((item is VideoGroup && item.presentCount == 0)) UiTools.snackerMissing(
                    requireActivity()
                ) else onLongClick(position)
            }

            is VideoCtxClick -> {
                when (item) {
                    is Folder -> {
                        val flags = createCtxFolderFlags().apply {
                            if (item.isFavorite) add(CTX_FAV_REMOVE) else add(CTX_FAV_ADD)
                        }
                        showContext(
                            requireActivity(), this@VideoGridFragment, position, item, flags
                        )
                    }

                    is VideoGroup -> {
                        if (item.presentCount == 0) UiTools.snackerMissing(requireActivity())
                        else {
                            val flags = createCtxVideoGroupFlags().apply {
                                if (item.isFavorite) add(CTX_FAV_REMOVE) else add(CTX_FAV_ADD)
                            }
                            showContext(
                                requireActivity(), this@VideoGridFragment, position, item, flags
                            )
                        }
                    }

                    is MediaWrapper -> {
                        val flags = createCtxVideoFlags().apply {
                            if (item.isFavorite) add(CTX_FAV_REMOVE) else add(CTX_FAV_ADD)
                            if (item.seen > 0) add(CTX_MARK_AS_UNPLAYED) else add(CTX_MARK_AS_PLAYED)
                            if (item.time != 0L) add(CTX_PLAY_FROM_START)
                            if (viewModel.groupingType == VideoGroupingType.NAME || viewModel.group != null) {
//                                if (viewModel.group != null) add(CTX_REMOVE_GROUP) else addAll(
//                                    CTX_ADD_GROUP, CTX_GROUP_SIMILAR
//                                )
                            }
                            //go to folder
                            if (item.uri.retrieveParent() != null) add(CTX_GO_TO_FOLDER)
                        }
                        showContext(
                            requireActivity(), this@VideoGridFragment, position, item, flags
                        )
                    }
                }
            }

            is VideoImageClick -> {
                if (actionMode != null) {
                    onClick(position, item)
                } else {
                    onLongClick(position)
                }
            }
        }
    }

    private fun onLongClick(position: Int) {
        if (actionMode == null && inSearchMode()) UiTools.setKeyboardVisibility(binding.root, false)
        multiSelectHelper.toggleSelection(position, true)
        if (actionMode == null) startActionMode() else invalidateActionMode()
    }

    private fun onClick(position: Int, item: MediaLibraryItem) {
        when (item) {
            is MediaWrapper -> {
                if (actionMode != null) {
                    multiSelectHelper.toggleSelection(position)
                    invalidateActionMode()
                } else {
                    viewModel.playVideo(activity, item, position)
                }
            }

            is Folder -> {
                if (item.mMrl.isMissing()) return
                if (actionMode != null) {
                    multiSelectHelper.toggleSelection(position)
                    invalidateActionMode()
                } else activity?.open(item)
            }

            is VideoGroup -> when {
                actionMode != null -> {
                    multiSelectHelper.toggleSelection(position)
                    invalidateActionMode()
                }

                item.presentCount == 0 -> UiTools.snackerMissing(requireActivity())
                item.presentCount == 1 -> viewModel.play(position)
                else -> activity?.open(item)
            }
        }
    }

    companion object {
        fun newInstance() = VideoGridFragment()
    }


    private fun Click.process() {
        if (position >= 0) {
            val item = historyViewModel.dataset.get(position)
            when (this) {
                is SimpleClick -> onClickHistory(position, item)
                is LongClick -> onLongClickHistory(position, item)
                is ImageClick -> {
                    if (actionMode != null) onClickHistory(position, item)
                    else onLongClickHistory(position, item)
                }

                else -> {}
            }
        }
    }

    private fun onClickHistory(position: Int, item: MediaWrapper) {
        if (KeyHelper.isShiftPressed && actionMode == null) {
            onLongClickHistory(position, item)
            return
        }
        if (actionMode != null) {
            multiSelectHelper.toggleSelection(position)
            historyAdapter.notifyItemChanged(position, item)
            invalidateActionMode()
            return
        }
        if (position != 0) historyViewModel.moveUp(item)
        MediaUtils.openMedia(requireContext(), item)
    }

    private fun onLongClickHistory(position: Int, item: MediaWrapper) {
        multiSelectHelperHistory.toggleSelection(position, true)
        historyAdapter.notifyItemChanged(position, item)
        if (actionMode == null) startActionMode()
        invalidateActionMode()
    }
}

sealed class VideoAction
class VideoClick(val position: Int, val item: MediaLibraryItem) : VideoAction()
class VideoLongClick(val position: Int, val item: MediaLibraryItem) : VideoAction()
class VideoCtxClick(val position: Int, val item: MediaLibraryItem) : VideoAction()
class VideoImageClick(val position: Int, val item: MediaLibraryItem) : VideoAction()