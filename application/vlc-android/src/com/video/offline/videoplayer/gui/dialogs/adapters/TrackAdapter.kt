package com.video.offline.videoplayer.gui.dialogs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.videolan.tools.Settings
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.VideoTrackItemBinding
import com.video.offline.videoplayer.gui.helpers.MARQUEE_ACTION
import com.video.offline.videoplayer.gui.helpers.enableMarqueeEffect
import com.video.offline.videoplayer.util.LifecycleAwareScheduler

class TrackAdapter(private val tracks: Array<VlcTrack>, var selectedTrack: VlcTrack?, val trackTypePrefix:String) : RecyclerView.Adapter<TrackAdapter.ViewHolder>() {

    lateinit var trackSelectedListener: (VlcTrack) -> Unit
    private var scheduler: LifecycleAwareScheduler? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VideoTrackItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun setOnTrackSelectedListener(listener: (VlcTrack) -> Unit) {
        trackSelectedListener = listener
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tracks[position], tracks[position] == selectedTrack)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (Settings.listTitleEllipsize == 4) scheduler = enableMarqueeEffect(recyclerView)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        scheduler?.cancelAction(MARQUEE_ACTION)
        super.onViewRecycled(holder)
    }


    inner class ViewHolder(val binding: VideoTrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            itemView.setOnClickListener {
                val oldSelectedIndex = tracks.indexOf(selectedTrack)
                selectedTrack = tracks[layoutPosition]
                notifyItemChanged(oldSelectedIndex)
                notifyItemChanged(layoutPosition)
                trackSelectedListener.invoke(tracks[layoutPosition])
            }
        }

        fun bind(trackDescription: VlcTrack, selected: Boolean) {
            binding.track = trackDescription
            val context = binding.root.context
            binding.contentDescription = context.getString(R.string.talkback_track, trackTypePrefix, if (trackDescription.getId() == "-1") context.getString(R.string.disable_track) else trackDescription.getName(), if (selected) context.getString(R.string.selected) else "")
            binding.selected = selected
            binding.executePendingBindings()
        }
    }
}