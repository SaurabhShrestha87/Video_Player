package com.video.offline.videoplayer.gui.preferences.theme

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.video.offline.videoplayer.R

class ThemeAdapter(
    private var arrayList: ArrayList<String>,
    private var itemClickListener: ItemClickListener,
    private var context: Context,
) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {
    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        // Initialize view 
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_theme, parent, false
        )
        // Pass holder view 
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        // Set text on radio button 
        holder.imageView.background = getThemeBackground(arrayList[position])

        // Checked selected radio button 
        holder.radioButton.isChecked = (position == selectedPosition)

        if ((position == selectedPosition)) {
            holder.imageView.setImageResource(R.drawable.ic_check)
        } else {
            holder.imageView.setImageResource(0)
        }
        // set listener on radio button 
        holder.radioButton.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            // check condition
            if (b) {
                // When checked
                // update selected position
                selectedPosition = holder.adapterPosition
                // Call listener
                itemClickListener.onClick(
                    holder.absoluteAdapterPosition
                )
            }
        }
    }

    private fun getThemeBackground(str: String): Drawable? {
        return AppCompatResources.getDrawable(
            context, R.drawable.rounded_corners_audio
        )?.mutate().also {
            if (it != null) {
                Log.d("TAG", "getThemeBackground: $str")
                when (str) {
                    context.getString(R.string.daynight_follow_system_title) -> DrawableCompat.setTint(it, 0xFFff0000.toInt())
                    context.getString(R.string.daynight_title) -> DrawableCompat.setTint(it, 0xFFff0000.toInt())
                    context.getString(R.string.light_theme) -> DrawableCompat.setTint(it, 0xFFff0000.toInt())
                    context.getString(R.string.black_theme) -> DrawableCompat.setTint(it, 0xFF000000.toInt())
                    context.getString(R.string.purple_theme) -> DrawableCompat.setTint(it, 0xFF5353FF.toInt())
                    context.getString(R.string.orange_theme) -> DrawableCompat.setTint(it, 0xFFFFA653.toInt())
                    context.getString(R.string.pink_theme) -> DrawableCompat.setTint(it, 0xFFFF5391.toInt())
                    context.getString(R.string.red_theme) -> DrawableCompat.setTint(it, 0xFFD1183E.toInt())
                    else -> DrawableCompat.setTint(it, 0xFFff0000.toInt())
                }
            }
        }
    }

    private fun getThemeColorId(str: String): Int {
        return when (str) {
            "daynight_follow_system_title" -> -1
            "daynight_title" -> 0
            "light_theme" -> 1
            "black_theme" -> 2
            "purple_theme" -> 3
            "orange_theme" -> 4
            "pink_theme" -> 5
            "red_theme" -> 6
            else -> -1
        }
    }

    override fun getItemId(position: Int): Long {
        // pass position 
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        // pass position 
        return position
    }

    override fun getItemCount(): Int {
        // pass total list size 
        return arrayList.size
    }

    inner class ViewHolder
        (itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Assign variable
        var radioButton: RadioButton = itemView.findViewById(R.id.radio_button)
        var imageView: ImageView = itemView.findViewById(R.id.check_img)
    }
}