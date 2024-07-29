package com.video.offline.videoplayer.gui.helpers

import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.video.offline.videoplayer.BR
import com.video.offline.videoplayer.R

open class SelectorViewHolder<T : ViewDataBinding>(vdb: T) : RecyclerView.ViewHolder(vdb.root) {

    var binding: T = vdb
    private val ITEM_FOCUS_ON: Int = ContextCompat.getColor(vdb.root.context, R.color.greentransparent)
    private val ITEM_FOCUS_OFF: Int = ContextCompat.getColor(vdb.root.context, R.color.transparent)
    private val ITEM_SELECTION_ON: Int = ContextCompat.getColor(vdb.root.context, R.color.greenLighttransparent)

    protected open fun isSelected() = false

    init {
        itemView.setOnFocusChangeListener { _, hasFocus -> if (layoutPosition >= 0) setViewBackground(hasFocus, isSelected()) }
    }

    open fun selectView(selected: Boolean) {
        setViewBackground(itemView.hasFocus(), selected)
    }

    private fun setViewBackground(focus: Boolean, selected: Boolean) {
        val color = if (focus) ITEM_FOCUS_ON else if (selected) ITEM_SELECTION_ON else ITEM_FOCUS_OFF
        binding.setVariable(BR.bgColor, color)
    }

}
