package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceViewHolder
import androidx.preference.TwoStatePreference
import androidx.recyclerview.widget.RecyclerView
import com.video.offline.videoplayer.R

class ClickableListPreference(context: Context, val attrs: AttributeSet) :
    ListPreference(context, attrs, androidx.preference.R.attr.preferenceScreenStyle, 0) {

    private var switchView: View? = null
    private var rootView: View? = null
    private var switchClickListener: View.OnClickListener? = null
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
            else -> { //2
                setBackground(R.drawable.pref_bottom_card_background)
            }
        }
        setMargins(20, 0, 20, 0)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        rootView = holder.itemView
        //for some reason, it does not initialize itself;
        setMargins(20, 0, 20, 0)
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

    fun setOnSwitchClickListener(listener: View.OnClickListener) {
        switchClickListener = listener
    }

    override fun onClick() {
        if (switchClickListener == null) {
            super.onClick()
        }
        //Do not call super.onClick();
    }
}