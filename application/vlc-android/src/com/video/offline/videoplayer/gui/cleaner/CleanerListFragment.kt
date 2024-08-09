package com.video.offline.videoplayer.gui.cleaner

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ActionMode
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.InitialPagedList
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.VideoCleanerListBinding
import com.video.offline.videoplayer.gui.ContentActivity
import com.video.offline.videoplayer.gui.browser.MediaBrowserFragment
import com.video.offline.videoplayer.gui.dialogs.CURRENT_SORT
import com.video.offline.videoplayer.gui.dialogs.CtxActionReceiver
import com.video.offline.videoplayer.gui.dialogs.DISPLAY_IN_CARDS
import com.video.offline.videoplayer.gui.dialogs.DISPLAY_MODE
import com.video.offline.videoplayer.gui.dialogs.DisplaySettingsDialog
import com.video.offline.videoplayer.gui.dialogs.ONLY_FAVS
import com.video.offline.videoplayer.gui.dialogs.SettingSortsDialog
import com.video.offline.videoplayer.gui.dialogs.VIDEO_GROUPING
import com.video.offline.videoplayer.gui.helpers.ItemOffsetDecoration
import com.video.offline.videoplayer.gui.helpers.fillActionMode
import com.video.offline.videoplayer.gui.video.VideoAction
import com.video.offline.videoplayer.gui.video.VideoBrowserFragment
import com.video.offline.videoplayer.gui.video.VideoClick
import com.video.offline.videoplayer.gui.video.VideoCtxClick
import com.video.offline.videoplayer.gui.video.VideoImageClick
import com.video.offline.videoplayer.gui.video.VideoLongClick
import com.video.offline.videoplayer.gui.view.EmptyLoadingState.EMPTY_CLEANER
import com.video.offline.videoplayer.gui.view.EmptyLoadingState.LOADING
import com.video.offline.videoplayer.gui.view.EmptyLoadingState.MISSING_PERMISSION
import com.video.offline.videoplayer.gui.view.EmptyLoadingState.NONE
import com.video.offline.videoplayer.interfaces.IRefreshable
import com.video.offline.videoplayer.providers.medialibrary.VideosProvider
import com.video.offline.videoplayer.reloadLibrary
import com.video.offline.videoplayer.util.ContextOption
import com.video.offline.videoplayer.util.Permissions
import com.video.offline.videoplayer.util.launchWhenStarted
import com.video.offline.videoplayer.util.onAnyChange
import com.video.offline.videoplayer.viewmodels.DisplaySettingsViewModel
import com.video.offline.videoplayer.viewmodels.mobile.VideoGroupingType
import com.video.offline.videoplayer.viewmodels.mobile.VideosViewModel
import com.video.offline.videoplayer.viewmodels.mobile.getViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.EventTools
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.AppContextProvider
import org.videolan.resources.GROUP_VIDEOS_FOLDER
import org.videolan.resources.GROUP_VIDEOS_NONE
import org.videolan.resources.KEY_CLEANER
import org.videolan.resources.KEY_CLEANER_BIG
import org.videolan.resources.KEY_CLEANER_WATCHED
import org.videolan.resources.KEY_FOLDER
import org.videolan.resources.KEY_GROUP
import org.videolan.resources.KEY_GROUPING
import org.videolan.resources.KEY_GROUP_VIDEOS
import org.videolan.resources.KEY_VIDEOS_CARDS
import org.videolan.resources.KEY_VIDEOS_COMPACT
import org.videolan.resources.KEY_VIDEOS_LIST
import org.videolan.resources.util.parcelable
import org.videolan.resources.util.waitForML
import org.videolan.tools.MultiSelectHelper
import org.videolan.tools.PLAYBACK_HISTORY
import org.videolan.tools.RESULT_RESTART
import org.videolan.tools.Settings
import org.videolan.tools.dp
import org.videolan.tools.putSingle
import java.io.File


private const val TAG = "VLC/CleanerListFragment"

private const val KEY_SELECTION = "key_selection"

class CleanerListFragment : MediaBrowserFragment<VideosViewModel>(),
    SwipeRefreshLayout.OnRefreshListener, CtxActionReceiver, IRefreshable, View.OnClickListener {

    private lateinit var dataObserver: RecyclerView.AdapterDataObserver
    private lateinit var videoListAdapter: CleanerListAdapter
    private lateinit var multiSelectHelper: MultiSelectHelper<MediaLibraryItem>
    private lateinit var binding: VideoCleanerListBinding
    private var gridItemDecoration: RecyclerView.ItemDecoration? = null
    private lateinit var settings: SharedPreferences
    private var savedSelection = ArrayList<Int>()
    private var cleanerType = KEY_CLEANER_WATCHED

    //in case of fragment being hosted by other fragments, it's useful to prevent the
    //FAB visibility to be locked hidden
    override val isMainNavigationPoint = false
    override val hasTabs: Boolean
        get() = parentFragment != null

    private val displaySettingsViewModel: DisplaySettingsViewModel by activityViewModels()

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (savedInstanceState?.getIntegerArrayList(KEY_SELECTION))?.let {
            savedSelection = it
        }
        if (!::settings.isInitialized) settings = Settings.getInstance(requireContext())
        if (!::videoListAdapter.isInitialized) {
            val seenMarkVisible = settings.getBoolean("media_seen", true)
            videoListAdapter = CleanerListAdapter(
                seenMarkVisible,
                !Settings.getInstance(requireActivity()).getBoolean(PLAYBACK_HISTORY, true)
            ).apply {
                stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            dataObserver = videoListAdapter.onAnyChange {
                updateEmptyView()
                if (::binding.isInitialized) binding.fastScroller.setRecyclerView(
                    binding.videoGrid, viewModel.provider
                )
            }
            multiSelectHelper = videoListAdapter.multiSelectHelper
            val folder =
                if (savedInstanceState != null) savedInstanceState.parcelable<Folder>(KEY_FOLDER)
                else arguments?.parcelable(KEY_FOLDER)
            val parentGroup =
                if (savedInstanceState != null) savedInstanceState.parcelable<VideoGroup>(KEY_GROUP)
                else arguments?.parcelable(KEY_GROUP)
            Log.w(TAG, "onCreate: ${savedInstanceState?.getString(KEY_CLEANER)}")
            cleanerType = when {
                savedInstanceState?.getString(KEY_CLEANER) != null -> savedInstanceState.getString(
                    KEY_CLEANER
                )!!

                arguments?.getString(KEY_CLEANER) != null -> arguments?.getString(KEY_CLEANER)!!
                else -> KEY_CLEANER_WATCHED
            }
            Log.d(TAG, "onCreate: $cleanerType")

            val grouping =
                if (parentGroup != null || folder != null) VideoGroupingType.NONE else when (Settings.getInstance(
                    requireContext()
                ).getString(KEY_GROUP_VIDEOS, null) ?: GROUP_VIDEOS_NONE) {
                    GROUP_VIDEOS_NONE -> VideoGroupingType.NONE
                    GROUP_VIDEOS_FOLDER -> VideoGroupingType.FOLDER
                    else -> VideoGroupingType.NONE
                }
            viewModel = getViewModel(grouping, folder, parentGroup)
            setDataObservers()
            EventTools.getInstance().lastThumb.observe(this, thumbObs)
            videoListAdapter.events.onEach { it.process() }.launchWhenStarted(lifecycleScope)
        }
    }

    private fun setDataObservers() {
        videoListAdapter.dataType = viewModel.groupingType
        videoListAdapter.showFilename.set(viewModel.groupingType == VideoGroupingType.NONE && viewModel.provider.sort == Medialibrary.SORT_FILENAME)
        viewModel.provider.isCleaner(cleanerType)
        lifecycleScope.launch {
            waitForML()
            viewModel.provider.pagedList.observe(this@CleanerListFragment) {
                @Suppress("UNCHECKED_CAST") (it as? PagedList<MediaLibraryItem>)?.let { pagedList ->
                    submitList(pagedList)
                }
                if (it !is InitialPagedList<*, *> && activity?.isFinishing == false && viewModel.group != null && it.size < 2 && viewModel.filterQuery.isNullOrEmpty()) requireActivity().finish()
            }
        }
        EventTools.getInstance().lastThumb.observe(this) {
            videoListAdapter.updateThumb(it)
        }
    }

    private fun selectAllData() {
        videoListAdapter.all.forEachIndexed { index, _ ->
            multiSelectHelper.toggleSelectionCleaner(index, forceSelection = true)
        }
    }

    private fun submitList(pagedList: PagedList<MediaLibraryItem>) {
        videoListAdapter.submitList(
            pagedList
        )
        val mutableList = pagedList.toMutableList()
        when (cleanerType) {
            KEY_CLEANER_WATCHED -> {
                mutableList.removeIf {
                    it is MediaWrapper && it.seen == 0L
                }
            }

            KEY_CLEANER_BIG -> {
                mutableList.removeIf {
                    it is MediaWrapper && File(Uri.decode(it.location.substring(5))).length() < 50 * 1024 * 1024
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cleaner_check_all -> {
                if (item.isChecked) {
                    videoListAdapter.all.forEachIndexed { index, _ ->
                        multiSelectHelper.clearSelection()
                    }
                } else {
                    videoListAdapter.all.forEachIndexed { index, _ ->
                        multiSelectHelper.toggleSelectionCleaner(index, forceSelection = true)
                    }
                }
                setAllSelectedMenu(!item.isChecked)
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.cleaner_check_all).isVisible = true

        menu.findItem(R.id.ml_menu_last_playlist).isVisible = false
        menu.findItem(R.id.ml_menu_select).isVisible = false
        menu.findItem(R.id.ml_menu_network_stream).isVisible = false
        menu.findItem(R.id.ml_menu_refresh).isVisible = false
        menu.findItem(R.id.ml_menu_filter).isVisible = false
        menu.findItem(R.id.ml_menu_last_playlist).isVisible = false
        menu.findItem(R.id.ml_menu_sortby).isVisible = false
        menu.findItem(R.id.ml_menu_theme).isVisible = false
        menu.findItem(R.id.ml_menu_equalizer).isVisible = false
        menu.findItem(R.id.ml_menu_settings).isVisible = false
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
        updateTitle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = VideoCleanerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as ContentActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_up)
        val empty = viewModel.isEmpty()
        binding.emptyLoading.state = if (empty) LOADING else NONE
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

        binding.cleanBtn.setOnClickListener {
            if (multiSelectHelper.getSelection().isEmpty()) {
                return@setOnClickListener
            }
            cleanItems(multiSelectHelper.getSelection()) {
                multiSelectHelper.clearSelection()
                updateEmptyView()
            }
        }
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
        fabPlay?.setImageResource(R.drawable.ic_tv_list_delete)
        fabPlay?.contentDescription = getString(R.string.clean)
        if (!viewModel.isEmpty() && getFilterQuery() == null) viewModel.refresh()
    }

    override fun onStop() {
        super.onStop()
        unregisterForContextMenu(binding.videoGrid)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_FOLDER, viewModel.folder)
        outState.putParcelable(KEY_GROUP, viewModel.group)
        outState.putSerializable(KEY_GROUPING, viewModel.groupingType)
    }

    override fun onDestroy() {
        super.onDestroy()
        gridItemDecoration = null
        swipeRefreshLayout.setOnRefreshListener(null)
        if (::dataObserver.isInitialized) videoListAdapter.unregisterAdapterDataObserver(
            dataObserver
        )
    }

    override fun getTitle() = "0 Selected"

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
        when (v) {
            binding.sortIv -> {
                //Open the display settings Bottom sheet
                SettingSortsDialog.newInstance(
                    currentSort = viewModel.provider.sort,
                    currentSortDesc = viewModel.provider.desc,
                    videoGroup = settings.getString(KEY_GROUP_VIDEOS, GROUP_VIDEOS_NONE)
                        ?: GROUP_VIDEOS_NONE
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
        Toast.makeText(requireContext(), "cant click fab here", Toast.LENGTH_SHORT).show()
    }

    private fun updateEmptyView() {
        if (!::binding.isInitialized) return
        if (!isAdded) return
        val empty = viewModel.isEmpty() && videoListAdapter.currentList.isNullOrEmpty()
        val working = viewModel.provider.loading.value != false
        binding.emptyLoading.emptyText = getString(R.string.nomediatoclean)
        binding.emptyLoading.state = when {
            !Permissions.canReadStorage(AppContextProvider.appContext) && empty -> MISSING_PERMISSION
            empty && working -> LOADING
            empty && !working -> EMPTY_CLEANER
            else -> NONE
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
        updateTitle()
        if (multiSelectHelper.getSelection().isNotEmpty() && !empty && !working) {
            lifecycleScope.launch {
                var bigItemFileLength = 0L
                multiSelectHelper.getSelection().forEach {
                    if (it is MediaWrapper) {
                        val itemFileLength =
                            withContext(Dispatchers.IO) { File(Uri.decode(it.location.substring(5))) }.length()
                        // if itemFileLength is greater than 50 MB, add it to bigFileList
                        bigItemFileLength += itemFileLength
                    }
                }
                val bigFilesSize = Formatter.formatFileSize(requireContext(), bigItemFileLength)

                binding.cleanBtn.text = getString(R.string.cleaner_clean_up_size, bigFilesSize)

            }
        } else {
            binding.cleanBtn.text = getString(R.string.cleaner_clean_up_size, "0 MB")
        }
    }

    override fun onRefresh() {
        activity?.reloadLibrary()
    }

    override fun setFabPlayVisibility(enable: Boolean) {
        super.setFabPlayVisibility(false)
    }


    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val count = multiSelectHelper.getSelectionCount()
        if (count == 0) {
            return false
        }
        lifecycleScope.launch { fillActionMode(requireActivity(), mode, multiSelectHelper) }
        return true
    }

    override fun onCtxAction(position: Int, option: ContextOption) {
        Toast.makeText(requireContext(), "Can't perform context Action!!", Toast.LENGTH_SHORT)
            .show()
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
                onClick(position)
            }

            is VideoLongClick -> {
                Toast.makeText(requireContext(), "Can't perform long click!", Toast.LENGTH_SHORT)
                    .show()
            }

            is VideoCtxClick -> {
                Toast.makeText(requireContext(), "Can't perform context click!", Toast.LENGTH_SHORT)
                    .show()
            }

            is VideoImageClick -> {
                if (actionMode != null) {
                    onClick(position)
                } else {
                    Toast.makeText(requireContext(), "actionMode null!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onClick(position: Int) {
        multiSelectHelper.toggleSelection(position)
        updateTitle()
        if(multiSelectHelper.getSelectionCount() < videoListAdapter.itemCount) {
            setAllSelectedMenu(false)
        } else if (multiSelectHelper.getSelectionCount() == videoListAdapter.itemCount) {
            setAllSelectedMenu(true)
        }
    }

    private fun setAllSelectedMenu(enabled: Boolean = false) {
        if (enabled) {
            menu?.findItem(R.id.cleaner_check_all)
                ?.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.circle_check))
        } else {
            menu?.findItem(R.id.cleaner_check_all)?.setIcon(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.circle_uncheck
                )
            )
        }
        menu?.findItem(R.id.cleaner_check_all)?.isChecked = enabled
        updateTitle()
    }

    private fun updateTitle() {
        (activity as? AppCompatActivity)?.run {
            supportActionBar?.title = "${multiSelectHelper.getSelectionCount()} Selected"
        }
    }

    companion object {
        fun newInstance(type: String) = CleanerListFragment().apply {
            arguments = bundleOf(KEY_CLEANER to type)
        }
    }

    override fun refresh() {

    }


}