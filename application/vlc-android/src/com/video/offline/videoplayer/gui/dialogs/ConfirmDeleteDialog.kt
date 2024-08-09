package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.helpers.UiTools.showPinIfNeeded
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Album
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.Playlist
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.util.parcelableList

const val CONFIRM_DELETE_DIALOG_MEDIALIST = "CONFIRM_DELETE_DIALOG_MEDIALIST"
const val CONFIRM_DELETE_DIALOG_TITLE = "CONFIRM_DELETE_DIALOG_TITLE"
const val CONFIRM_DELETE_DIALOG_DESCRIPTION = "CONFIRM_DELETE_DIALOG_DESCRIPTION"
const val CONFIRM_DELETE_DIALOG_BUTTON_TEXT = "CONFIRM_DELETE_DIALOG_BUTTON_TEXT"

class ConfirmDeleteDialog : DialogFragment() {

    private lateinit var listener: () -> Unit
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var deleteButton: Button
    private lateinit var mediaList: List<MediaLibraryItem>
    private var titleString: String? = null
    private var descriptionString: String? = null
    private var buttonText: String? = null

    companion object {

        /**
         * Create a new ConfirmDeleteDialog
         * @param medias the list of media used to create the title. If not relevant, use [title], [description] and [buttonText]
         * @param title the title to be used
         * @param description the description to be used
         * @param buttonText the button's text to be used
         */
        fun newInstance(
            medias: ArrayList<MediaLibraryItem> = arrayListOf(),
            title: String = "",
            description: String = "",
            buttonText: String = "",
        ): ConfirmDeleteDialog {

            return ConfirmDeleteDialog().apply {
                arguments = bundleOf(
                    CONFIRM_DELETE_DIALOG_MEDIALIST to medias,
                    CONFIRM_DELETE_DIALOG_TITLE to title,
                    CONFIRM_DELETE_DIALOG_DESCRIPTION to description,
                    CONFIRM_DELETE_DIALOG_BUTTON_TEXT to buttonText
                )
            }
        }
    }

    fun setListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch { if (requireActivity().showPinIfNeeded()) dismiss() }
        mediaList = arguments?.parcelableList(CONFIRM_DELETE_DIALOG_MEDIALIST) ?: listOf()
        titleString = arguments?.getString(CONFIRM_DELETE_DIALOG_TITLE)
        descriptionString = arguments?.getString(CONFIRM_DELETE_DIALOG_DESCRIPTION)
        buttonText = arguments?.getString(CONFIRM_DELETE_DIALOG_BUTTON_TEXT)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.dialog_confirm_delete, container)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_dialog)
        title = view.findViewById(R.id.title)
        description = view.findViewById(R.id.message)
        deleteButton = view.findViewById(R.id.delete_button)
        view.findViewById<Button>(R.id.delete_button).setOnClickListener {
            listener.invoke()
            dismiss()
        }
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            dismiss()
        }

        title.text = when {
            mediaList.isEmpty() -> titleString
            mediaList.size > 1 && mediaList.filterIsInstance<MediaWrapper>().size == mediaList.size -> {
                //folders and files
                val nbFiles =
                    mediaList.filter { it is MediaWrapper && it.type != MediaWrapper.TYPE_DIR }.size
                val nbFolders =
                    mediaList.filter { it is MediaWrapper && it.type == MediaWrapper.TYPE_DIR }.size
                when {
                    nbFiles == 0 -> getString(R.string.confirm_delete_folders, nbFolders)
                    nbFolders == 0 -> getString(R.string.confirm_delete_files, nbFiles)
                    else -> getString(R.string.confirm_delete_folders_and_files, nbFolders, nbFiles)
                }
            }

            mediaList[0] is MediaWrapper -> getString(
                if ((mediaList[0] as MediaWrapper).type == MediaWrapper.TYPE_DIR) R.string.confirm_delete_folder else R.string.confirm_delete,
                mediaList[0].title
            )

            mediaList[0] is Album -> getString(R.string.confirm_delete_album, mediaList[0].title)
            mediaList[0] is Playlist -> getString(
                R.string.confirm_delete_playlist, mediaList[0].title
            )

            else -> getString(R.string.confirm_delete_several_media, mediaList.size)
        }

        if (descriptionString?.isNotEmpty() == true) description.text = descriptionString
        if (buttonText?.isNotEmpty() == true) deleteButton.text = buttonText

        return view
    }
}