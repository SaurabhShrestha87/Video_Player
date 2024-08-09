package com.video.offline.videoplayer.gui.browser

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.format.Formatter
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager

import com.google.android.material.bottomsheet.BottomSheetDialog

import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.BaseFragment
import com.video.offline.videoplayer.gui.dialogs.ConfirmDeleteDialog
import com.video.offline.videoplayer.gui.dialogs.ConfirmMakePrivateDialog
import com.video.offline.videoplayer.gui.dialogs.RenameDialog
import com.video.offline.videoplayer.gui.helpers.UiTools
import com.video.offline.videoplayer.gui.helpers.fillActionMode
import com.video.offline.videoplayer.interfaces.Filterable
import com.video.offline.videoplayer.media.MediaUtils
import com.video.offline.videoplayer.media.getAll
import com.video.offline.videoplayer.util.makePrivate
import com.video.offline.videoplayer.util.makePrivateFolderItems
import com.video.offline.videoplayer.viewmodels.DisplaySettingsViewModel
import com.video.offline.videoplayer.viewmodels.MedialibraryViewModel
import com.video.offline.videoplayer.viewmodels.SortableModel
import com.video.offline.videoplayer.viewmodels.prepareOptionsMenu
import com.video.offline.videoplayer.viewmodels.sortMenuTitles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Folder
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.tools.MultiSelectHelper

import java.io.File




private const val TAG = "VLC/MediaBrowserFragment"
private const val KEY_SELECTION = "key_selection"

abstract class MediaBrowserFragment<T : SortableModel> : BaseFragment(), Filterable {

    private lateinit var searchButtonView: View
    lateinit var mediaLibrary: Medialibrary
    private var savedSelection = ArrayList<Int>()
    private val transition = ChangeBounds().apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = 300
    }

    private val displaySettingsViewModel: DisplaySettingsViewModel by activityViewModels()

    /**
     * Triggered when a display setting is changed
     *
     * @param key the display settings key
     * @param value the new display settings value
     */
    open fun onDisplaySettingChanged(key: String, value: Any) {}

    open lateinit var viewModel: T
        protected set

    abstract fun getMultiHelper(): MultiSelectHelper<T>?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaLibrary = Medialibrary.getInstance()
        (savedInstanceState?.getIntegerArrayList(KEY_SELECTION))?.let { savedSelection = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButtonView = view.findViewById(R.id.searchButton)
        viewLifecycleOwner.lifecycleScope.launch {
            //listen to display settings changes
            displaySettingsViewModel.settingChangeFlow.flowWithLifecycle(
                viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
            ).collect {
                if (isResumed) {
                    onDisplaySettingChanged(it.key, it.value)
                    displaySettingsViewModel.consume()
                }
            }
        }

    }

    protected open fun setBreadcrumb() {
        activity?.findViewById<RecyclerView>(R.id.ariane)?.visibility = View.GONE
    }

    private fun releaseBreadCrumb() {
        activity?.findViewById<RecyclerView>(R.id.ariane)?.adapter = null
    }

    override fun onStart() {
        super.onStart()
        setBreadcrumb()
    }

    override fun onStop() {
        super.onStop()
        releaseBreadCrumb()
    }

    override fun onResume() {
        super.onResume()
        (viewModel as? MedialibraryViewModel)?.resume()
    }


    override fun onPause() {
        super.onPause()
        (viewModel as? MedialibraryViewModel)?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        savedSelection.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getMultiHelper()?.let {
            outState.putIntegerArrayList(KEY_SELECTION, it.selectionMap)
        }
        super.onSaveInstanceState(outState)
    }

    abstract fun onRefresh()
    open fun clear() {}

    open fun showCleanedBottomSheet(items: List<MediaLibraryItem>) {
        lifecycleScope.launch {
            var bigItemFileLength = 0L
            items.forEach {
                if (it is MediaWrapper) {
                    val itemFileLength =
                        withContext(Dispatchers.IO) { File(Uri.decode(it.location.substring(5))) }.length()
                    // if itemFileLength is greater than 50 MB, add it to bigFileList
                    bigItemFileLength += itemFileLength
                }
            }
            val bigFilesSize = Formatter.formatFileSize(requireContext(), bigItemFileLength)
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.CleanerTransparentTheme
            )
            val bottomSheetView =
                LayoutInflater.from(requireContext()).inflate(R.layout.cleaner_bottom_sheet, null)
            val text: String = ("Free up $bigFilesSize space!")
            val spannable: Spannable = SpannableString(text)
            spannable.setSpan(
                ForegroundColorSpan(requireContext().getColor(R.color.colorPrimary)),
                "Free up ".length,
                ("Free up $bigFilesSize").length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            bottomSheetView.findViewById<TextView>(R.id.free_up_text)
                .setText(spannable, TextView.BufferType.SPANNABLE)
            bottomSheetView.findViewById<Button>(R.id.cleaningButton).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

        }
    }

    protected open fun cleanItem(item: MediaLibraryItem) {
        MediaUtils.deleteItem(requireActivity(), item) {
            onDeleteFailed(it)
        }
    }

    protected open fun cleanItems(items: List<MediaLibraryItem>) {
        if (items.size == 1) {
            cleanItem(items[0])
        } else {
            for (item in items) {
                cleanItem(item)
            }
        }
        showCleanedBottomSheet(items)
    }

    protected open fun removeItems(items: List<MediaLibraryItem>) {
        if (items.size == 1) {
            removeItem(items[0])
            return
        }
        val dialog = ConfirmDeleteDialog.newInstance(ArrayList(items))
        dialog.show(requireActivity().supportFragmentManager, ConfirmDeleteDialog::class.simpleName)
        dialog.setListener {
            for (item in items) {
                MediaUtils.deleteItem(requireActivity(), item) { onDeleteFailed(it) }
            }
        }
    }

    protected open fun renameFile(media: MediaLibraryItem) {
        val dialog = RenameDialog.newInstance(media)
        dialog.setListener { item, name ->
            MediaUtils.renameFile(requireActivity(), item, name) { onRenameFailed(it) }
            (activity as? AppCompatActivity)?.run {
                supportActionBar?.title = name
            }
        }
        dialog.show(requireActivity().supportFragmentManager, RenameDialog::class.simpleName)
    }

    protected open fun renameItem(item: MediaLibraryItem): Boolean {
        val dialog = ConfirmDeleteDialog.newInstance(arrayListOf(item))
        dialog.show(requireActivity().supportFragmentManager, ConfirmDeleteDialog::class.simpleName)
        dialog.setListener {
            MediaUtils.deleteItem(requireActivity(), item) { onDeleteFailed(it) }
        }
        return true
    }

    protected open fun removeItem(item: MediaLibraryItem): Boolean {
        val dialog = ConfirmDeleteDialog.newInstance(arrayListOf(item))
        dialog.show(requireActivity().supportFragmentManager, ConfirmDeleteDialog::class.simpleName)
        dialog.setListener {
            MediaUtils.deleteItem(requireActivity(), item) { onDeleteFailed(it) }
        }
        return true
    }


    protected open fun makePrivateItem(items: ArrayList<MediaLibraryItem>): Boolean {
        val dialog = ConfirmMakePrivateDialog.newInstance(items)
        dialog.show(
            requireActivity().supportFragmentManager, ConfirmMakePrivateDialog::class.simpleName
        )
        dialog.setListener {
            lifecycleScope.launch {
                (requireActivity() as AppCompatActivity).makePrivateFolderItems(items as ArrayList<MediaWrapper>)
            }
        }
        return true
    }

    protected open fun makePrivateFolder(folders: List<Folder>): Boolean {
        // Flatten the list of media items from all folders
        val medias = folders.flatMap { folder1: Folder -> folder1.getAll() }
        // Safely map the items to MediaLibraryItem
        val mediaLibraryItemList: ArrayList<MediaLibraryItem> =
            ArrayList(medias.filterIsInstance<MediaLibraryItem>())
        // Create the dialog instance with the media library items
        val dialog = ConfirmMakePrivateDialog.newInstance(mediaLibraryItemList)
        dialog.show(
            requireActivity().supportFragmentManager, ConfirmMakePrivateDialog::class.simpleName
        )
        dialog.setListener {
            lifecycleScope.launch {
                (requireActivity() as AppCompatActivity).makePrivateFolderItems(
                    medias
                )
            }
        }
        return true
    }

    protected open fun makePrivateItem(item: MediaLibraryItem): Boolean {
        val dialog = ConfirmMakePrivateDialog.newInstance(arrayListOf(item))
        dialog.show(
            requireActivity().supportFragmentManager,
            ConfirmMakePrivateDialog::class.simpleName
        )
        dialog.setListener {
            lifecycleScope.launch { (requireActivity() as AppCompatActivity).makePrivate(item as MediaWrapper) }
        }
        return true
    }

    private fun onRenameFailed(item: MediaLibraryItem) {
        if (isAdded) UiTools.snacker(
            requireActivity(), getString(R.string.msg_rename_failed, item.title)
        )
    }

    private fun onDeleteFailed(item: MediaLibraryItem) {
        if (isAdded) UiTools.snacker(
            requireActivity(),
            getString(R.string.msg_delete_failed, item.title)
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (viewModel as? MedialibraryViewModel)?.prepareOptionsMenu(menu)
        sortMenuTitles()
    }

    open fun sortMenuTitles(index: Int = 0) {
        menu?.let { (viewModel as? MedialibraryViewModel)?.sortMenuTitles(it, index) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ml_menu_sortby_name -> {
                sortBy(Medialibrary.SORT_ALPHA)
                return true
            }

            R.id.ml_menu_sortby_filename -> {
                sortBy(Medialibrary.SORT_FILENAME)
                return true
            }

            R.id.ml_menu_sortby_length -> {
                sortBy(Medialibrary.SORT_DURATION)
                return true
            }

            R.id.ml_menu_sortby_date -> {
                sortBy(Medialibrary.SORT_RELEASEDATE)
                return true
            }

            R.id.ml_menu_sortby_last_modified -> {
                sortBy(Medialibrary.SORT_LASTMODIFICATIONDATE)
                return true
            }

            R.id.ml_menu_sortby_insertion_date -> {
                sortBy(Medialibrary.SORT_INSERTIONDATE)
                return true
            }

            R.id.ml_menu_sortby_artist_name -> {
                sortBy(Medialibrary.SORT_ARTIST)
                return true
            }

            R.id.ml_menu_sortby_album_name -> {
                sortBy(Medialibrary.SORT_ALBUM)
                return true
            }

            R.id.ml_menu_sortby_media_number -> {
                sortBy(Medialibrary.NbMedia)
                return true
            }

            R.id.ml_menu_sortby_number -> {
                sortBy(Medialibrary.SORT_FILESIZE) //TODO
                return super.onOptionsItemSelected(item)
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    open fun sortBy(sort: Int) {
        viewModel.sort(sort)
    }

    fun restoreMultiSelectHelper() {
        getMultiHelper()?.let {

            if (savedSelection.size > 0) {
                var hasOneSelected = false
                for (i in 0 until savedSelection.size) {

                    it.selectionMap.add(savedSelection[i])
                    hasOneSelected = savedSelection.isNotEmpty()
                }
                if (hasOneSelected) startActionMode()
                savedSelection.clear()
            }
            if (actionMode != null) lifecycleScope.launch(Dispatchers.Main) {
                @Suppress("UNCHECKED_CAST") fillActionMode(
                    requireActivity(),
                    actionMode!!,
                    it as MultiSelectHelper<MediaLibraryItem>
                )
            }
        }
    }

    override fun filter(query: String) = viewModel.filter(query)

    override fun restoreList() = viewModel.restore()

    override fun enableSearchOption() = true

    override fun getFilterQuery() = viewModel.filterQuery

    override fun setSearchVisibility(visible: Boolean) {
        if (searchButtonView.visibility == View.VISIBLE == visible) return
        if (searchButtonView.parent is ConstraintLayout) {
            val cl = searchButtonView.parent as ConstraintLayout
            val cs = ConstraintSet()
            cs.clone(cl)
            cs.setVisibility(
                R.id.searchButton,
                if (visible) ConstraintSet.VISIBLE else ConstraintSet.GONE
            )
            transition.excludeChildren(RecyclerView::class.java, true)
            TransitionManager.beginDelayedTransition(cl, transition)
            cs.applyTo(cl)
        } else searchButtonView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun inSearchMode() = searchButtonView.visibility == View.VISIBLE

    override fun allowedToExpand() = true
}
