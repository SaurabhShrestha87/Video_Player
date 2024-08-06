package com.video.offline.videoplayer.gui.dialogs


import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.DialogInfoBinding
import com.video.offline.videoplayer.gui.InfoModel
import com.video.offline.videoplayer.gui.helpers.MedialibraryUtils
import com.video.offline.videoplayer.gui.video.MediaInfoAdapter
import com.video.offline.videoplayer.util.generateResolutionClass
import com.video.offline.videoplayer.util.getModel
import com.video.offline.videoplayer.util.getScreenWidth
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.Artist
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.TAG_ITEM
import org.videolan.resources.util.parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class InfoDialog : DialogFragment() {
    private lateinit var listener: (media: MediaLibraryItem, name: String) -> Unit
    private lateinit var item: MediaLibraryItem
    internal lateinit var binding: DialogInfoBinding
    private lateinit var model: InfoModel
    private lateinit var adapter: MediaInfoAdapter

    companion object {
        fun newInstance(item: MediaLibraryItem): InfoDialog {
            return InfoDialog().apply {
                arguments = bundleOf(TAG_ITEM to item)
            }
        }
    }

    fun setListener(listener: (media: MediaLibraryItem, name: String) -> Unit) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.parcelable(TAG_ITEM) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogInfoBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_dialog)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (item.id == 0L) {
            val libraryItem = Medialibrary.getInstance().getMedia((item as MediaWrapper).uri)
            if (libraryItem != null) this.item = libraryItem
        }
        binding.item = item

        model = getModel()

        if (item is MediaWrapper) {
            adapter = MediaInfoAdapter()
            binding.list.layoutManager = LinearLayoutManager(binding.root.context)
            binding.list.adapter = adapter
            binding.list.isNestedScrollingEnabled = false
            if (model.sizeText.value === null) model.checkFile(item as MediaWrapper)
            if (model.mediaTracks.value === null) model.parseTracks(
                requireContext(), item as MediaWrapper
            )
        }
        model.hasSubs.observe(this) {
//            if (it) binding.infoSubtitles.visibility = View.VISIBLE
        }
        model.mediaTracks.observe(this) { adapter.setTracks(it) }
        model.sizeText.observe(this) {
            binding.sizeValueText = Formatter.formatFileSize(requireContext(), it)
        }
        model.cover.observe(this) {
//            if (it != null) {
//                binding.cover = BitmapDrawable(requireActivity().resources, it)
//                lifecycleScope.launch {
//                    ViewCompat.setNestedScrollingEnabled(binding.container, true)
//                    binding.appbar.setExpanded(true, true)
//                }
//            } else noCoverFallback()
        }
        if (model.cover.value === null) model.getCover(item, requireActivity().getScreenWidth())
        updateMeta()
        binding.okayButton.setOnClickListener {
            dismiss()
        }
    }

    private fun formatMillisecondsToDate(milliseconds: Long): String {
        val date = Date(milliseconds)
        val format = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun updateMeta() = lifecycleScope.launchWhenStarted {
        var length = 0L
        val tracks = item.tracks
        val nbTracks = tracks?.size ?: 0
        if (nbTracks > 0) for (media in tracks!!) length += media.length
        if (length > 0) binding.length = length

        if (item is MediaWrapper) {
            val media = item as MediaWrapper
            val resolution = generateResolutionClass(media.width, media.height)
            binding.resolution = resolution
            binding.filepath = media.uri.path.toString()
            binding.format = media.uri.path.toString().substringAfterLast(".")
            val formattedDate = formatMillisecondsToDate(media.insertionDate * 1000)
            binding.date = formattedDate
            binding.artist = media.artist
            binding.album = media.album
        }

        binding.scanned = true
        when (item.itemType) {
            MediaLibraryItem.TYPE_MEDIA -> {
                val media = item as MediaWrapper
                binding.progress =
                    if (media.length == 0L) 0 else (100.toLong() * media.time / length).toInt()
                binding.sizeTitleText = getString(R.string.file_size)
            }

            MediaLibraryItem.TYPE_ARTIST -> {
                val albums = (item as Artist).albums
                val artist = (item as Artist).title
                binding.artist = artist
                binding.album = albums.joinToString(", ") { it.title }
            }

            else -> {
                binding.sizeTitleText = getString(R.string.tracks)
                binding.sizeValueText = nbTracks.toString()
            }
        }
    }
}
