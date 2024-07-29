package com.video.offline.videoplayer.television.ui.audioplayer

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import com.video.offline.videoplayer.television.R
import com.video.offline.videoplayer.television.databinding.TvPlaylistItemBinding
import com.video.offline.videoplayer.gui.DiffUtilAdapter
import com.video.offline.videoplayer.gui.helpers.SelectorViewHolder
import com.video.offline.videoplayer.gui.helpers.getBitmapFromDrawable
import com.video.offline.videoplayer.gui.view.MiniVisualizer
import com.video.offline.videoplayer.viewmodels.PlaylistModel

class PlaylistAdapter
internal constructor(private val audioPlayerActivity: AudioPlayerActivity, val model: PlaylistModel) : DiffUtilAdapter<MediaWrapper, PlaylistAdapter.ViewHolder>() {
    var selectedItem = -1
        private set
    private var currentPlayingVisu: MiniVisualizer? = null
    private var defaultCoverAudio: BitmapDrawable = BitmapDrawable(audioPlayerActivity.resources, getBitmapFromDrawable(audioPlayerActivity, R.drawable.ic_song_background))

    inner class ViewHolder(vdb: TvPlaylistItemBinding) : SelectorViewHolder<TvPlaylistItemBinding>(vdb), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            setSelection(layoutPosition)
            audioPlayerActivity.playSelection()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TvPlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.media = dataset[position]
        if (selectedItem == position) {
            if (model.playing) holder.binding.playing.start() else holder.binding.playing.stop()
            holder.binding.playing.visibility = View.VISIBLE
            holder.binding.coverImage.visibility = View.INVISIBLE
            currentPlayingVisu = holder.binding.playing
        } else {
            holder.binding.playing.stop()
            holder.binding.playing.visibility = View.INVISIBLE
            holder.binding.coverImage.visibility = View.VISIBLE
        }
        holder.binding.scaleType = ImageView.ScaleType.CENTER_CROP
        holder.binding.cover = defaultCoverAudio
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNullOrEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val isCurrent = payloads[0] as Boolean
            val shouldStart = isCurrent && model.playing
            if (shouldStart) holder.binding.playing.start() else holder.binding.playing.stop()
            if (isCurrent) {
                holder.binding.playing.visibility = View.VISIBLE
                holder.binding.coverImage.visibility = View.INVISIBLE
            } else {
                holder.binding.playing.visibility = View.INVISIBLE
                holder.binding.coverImage.visibility = View.VISIBLE
            }
            holder.binding.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.binding.cover = defaultCoverAudio

        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        currentPlayingVisu = null
    }

    fun setSelection(pos: Int) {
        if (pos == selectedItem) return
        val previous = selectedItem
        selectedItem = pos
        if (previous != -1) notifyItemChanged(previous, false)
        if (pos != -1) notifyItemChanged(selectedItem, true)
    }

    override fun onUpdateFinished() {
        audioPlayerActivity.onUpdateFinished()
    }

    companion object {
        const val TAG = "VLC/PlaylistAdapter"
    }
}