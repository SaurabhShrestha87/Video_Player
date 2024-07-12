package com.video.offline.videoplayer.moviepedia.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.video.offline.videoplayer.moviepedia.databinding.MoviepediaItemBinding
import com.video.offline.videoplayer.moviepedia.models.resolver.ResolverMedia
import org.videolan.tools.getLocaleLanguages
import com.video.offline.videoplayer.gui.helpers.SelectorViewHolder

class MediaScrapingResultAdapter internal constructor(private val layoutInflater: LayoutInflater) : RecyclerView.Adapter<MediaScrapingResultAdapter.ViewHolder>() {

    private var dataList: List<ResolverMedia>? = null
    internal lateinit var clickHandler: MediaScrapingActivity.ClickHandler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MoviepediaItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mediaResult = dataList!![position]
        holder.binding.item = mediaResult
        holder.binding.imageUrl = mediaResult.imageUri(layoutInflater.context.getLocaleLanguages())
    }

    fun setItems(newList: List<ResolverMedia>) {
        dataList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (dataList == null) 0 else dataList!!.size
    }

    inner class ViewHolder(binding: MoviepediaItemBinding) : SelectorViewHolder<MoviepediaItemBinding>(binding) {

        init {
            binding.handler = clickHandler
        }
    }
}

@BindingAdapter("year")
fun showYear(view: TextView, item: ResolverMedia) {
    view.text = item.year()
}
