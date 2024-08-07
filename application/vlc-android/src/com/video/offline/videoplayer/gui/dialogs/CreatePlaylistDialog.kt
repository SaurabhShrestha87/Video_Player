package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.DialogCreatePlaylistBinding
import com.video.offline.videoplayer.gui.helpers.UiTools
import com.video.offline.videoplayer.gui.helpers.UiTools.showPinIfNeeded
import com.video.offline.videoplayer.providers.FileBrowserProvider
import com.video.offline.videoplayer.viewmodels.browser.TYPE_FILE
import com.video.offline.videoplayer.viewmodels.browser.getBrowserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.resources.util.parcelableArray
import org.videolan.tools.CoroutineContextProvider
import org.videolan.tools.DependencyProvider
import org.videolan.tools.Settings
import java.util.concurrent.atomic.AtomicBoolean

class CreatePlaylistDialog(private val onPlaylistAdded: () -> Unit) : DialogFragment(),
    View.OnClickListener, TextView.OnEditorActionListener {

    private var isLoading: Boolean = false
        set(value) {
            field = value
            if (::binding.isInitialized) binding.isLoading = value
        }
    private var filesText: String = ""
        set(value) {
            field = value
            if (::binding.isInitialized) binding.filesText = value
        }
    private lateinit var binding: DialogCreatePlaylistBinding
    private lateinit var newTracks: Array<MediaWrapper>
    private lateinit var medialibrary: Medialibrary

    private val coroutineContextProvider: CoroutineContextProvider
    private val alreadyAdding = AtomicBoolean(false)

    init {
        CreatePlaylistDialog.registerCreator { CoroutineContextProvider() }
        coroutineContextProvider = CreatePlaylistDialog.get(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { if (requireActivity().showPinIfNeeded()) dismiss() }
        medialibrary = Medialibrary.getInstance()
        newTracks = try {
            @Suppress("UNCHECKED_CAST") val tracks =
                requireArguments().parcelableArray<MediaWrapper>(KEY_NEW_TRACKS) as Array<MediaWrapper>
            filesText =
                resources.getQuantityString(R.plurals.media_quantity, tracks.size, tracks.size)
            tracks
        } catch (e: Exception) {
            try {
                requireArguments().getString(KEY_FOLDER)?.let { folder ->

                    isLoading = true
                    val viewModel = getBrowserModel(category = TYPE_FILE, url = folder)
                    if (requireArguments().getBoolean(
                            KEY_SUB_FOLDERS, false
                        )
                    ) lifecycleScope.launchWhenStarted {
                        withContext(Dispatchers.IO) {
                            newTracks =
                                (viewModel.provider as FileBrowserProvider).browseByUrl(folder)
                                    .toTypedArray()
                            isLoading = false
                            filesText = resources.getQuantityString(
                                R.plurals.media_quantity, newTracks.size, newTracks.size
                            )
                        }
                    } else {
                        viewModel.dataset.observe(this) { mediaLibraryItems ->
                            newTracks = mediaLibraryItems.asSequence().map { it as MediaWrapper }
                                .filter { it.type != MediaWrapper.TYPE_DIR }.toList().toTypedArray()
                            isLoading = false
                            filesText = resources.getQuantityString(
                                R.plurals.media_quantity, newTracks.size, newTracks.size
                            )
                        }
                    }
                }
                emptyArray()
            } catch (e: Exception) {
                emptyArray()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogCreatePlaylistBinding.inflate(layoutInflater, container, false)
        binding.isLoading = isLoading
        binding.filesText = filesText
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_dialog)
        binding.dialogPlaylistCreate.setOnClickListener(this)

        binding.dialogPlaylistName.editText!!.setOnEditorActionListener(this)
        binding.dialogPlaylistName.editText!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                addNewPlaylist()
                true
            } else false
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_playlist_create -> addNewPlaylist()
        }
    }


    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) addNewPlaylist()
        return false
    }

    private fun addNewPlaylist() {
        if (alreadyAdding.getAndSet(true)) return
        val name =
            binding.dialogPlaylistName.editText?.text?.toString()?.trim { it <= ' ' } ?: return
        UiTools.setKeyboardVisibility(binding.dialogPlaylistName, false)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { medialibrary.getPlaylistByName(name) }?.let {
                binding.dialogPlaylistName.error = getString(R.string.playlist_existing, it.title)
                alreadyAdding.set(false)
                return@launch
            }
            medialibrary.createPlaylist(name, Settings.includeMissing, false)
            binding.dialogPlaylistName.editText?.text?.clear()
            alreadyAdding.set(false)
            binding.dialogPlaylistName.error = null
            onPlaylistAdded.invoke()
            dismiss()
        }
    }

    companion object : DependencyProvider<Any>() {

        const val TAG = "VLC/CreatePlaylistDialog"

        const val KEY_NEW_TRACKS = "PLAYLIST_NEW_TRACKS"
        const val KEY_FOLDER = "PLAYLIST_FROM_FOLDER"
        const val KEY_SUB_FOLDERS = "PLAYLIST_FOLDER_ADD_SUBFOLDERS"

        const val SELECTED_PLAYLIST = "SELECTED_PLAYLIST"
    }
}