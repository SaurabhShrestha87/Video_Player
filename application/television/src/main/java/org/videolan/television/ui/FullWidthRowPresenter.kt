package org.videolan.television.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.RowPresenter
import org.videolan.television.R

open class FullWidthRowPresenter : RowPresenter() {

    open inner class FullWidthRowPresenterViewHolder(view: View) : ViewHolder(view) {

//        init {
//            val container = view.findViewById<ConstraintLayout>(R.id.container)
//        }
    }

    init {
        selectEffectEnabled = false
    }

    override fun createRowViewHolder(parent: ViewGroup): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tv_description_row, parent, false)
        return FullWidthRowPresenterViewHolder(v)
    }

    override fun isUsingDefaultSelectEffect(): Boolean {
        return true
    }
}