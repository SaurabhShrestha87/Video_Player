package org.videolan.vlc.gui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.RecyclerView
import org.videolan.vlc.R

class ClickablePreferenceScreen(context: Context, val attrs: AttributeSet) :
    Preference(context, attrs, androidx.preference.R.attr.preferenceScreenStyle, 0) {

    private var rootView: View? = null
    private var prefClickListener: View.OnClickListener? = null
    private var position: Int = 1

    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.prefView, 0, 0)
        position = typedArray.getInt(R.styleable.prefView_pref_position, 1)
        typedArray.recycle()
    }

    fun init() {
        Log.d("TAG", "onBindViewHolder: $position")
        when (position) {
            0 -> {
                setBackground(R.drawable.pref_top_card_background)
            }

            1 -> {
                setBackground(R.drawable.pref_middle_card_background)
            }

            2 -> {
                setBackground(R.drawable.pref_middle_card_background)
            }

            else -> { //3
                setBackground(R.drawable.pref_single_card_background)
            }
        }
        setMargins(20, 0, 20, 0)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        rootView = holder.itemView
        if (prefClickListener != null) rootView!!.setOnClickListener(prefClickListener)
        init()
    }

    fun setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        val params = rootView?.layoutParams as RecyclerView.LayoutParams
        // Set the new margins (left, top, right, bottom)
        params.setMargins(left, top, right, bottom)
        rootView?.layoutParams = params
    }

    fun setBackground(drawable: Int) {
        rootView?.background = getDrawable(context, drawable)
    }

    fun setOnViewClickListener(listener: View.OnClickListener) {
        prefClickListener = listener
    }

    override fun onClick() {
        if (prefClickListener == null) {
            super.onClick()
        }
        //Do not call super.onClick();
    }
}