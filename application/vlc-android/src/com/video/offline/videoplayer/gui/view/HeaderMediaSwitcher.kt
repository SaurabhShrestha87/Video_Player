package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.video.offline.videoplayer.R

class HeaderMediaSwitcher(context: Context, attrs: AttributeSet) : AudioMediaSwitcher(context, attrs) {

    override fun addMediaView(inflater: LayoutInflater, title: String?, artist: String?, album: String?, cover: Bitmap?, trackInfo: String?) {
        val v = inflater.inflate(R.layout.audio_media_switcher_item, this, false)

        val coverView = v.findViewById<View>(R.id.cover) as ImageView
        val titleView = v.findViewById<View>(R.id.title) as TextView
        val artistView = v.findViewById<View>(R.id.artist) as TextView

        if (cover != null) {
            coverView.visibility = View.VISIBLE
            coverView.setImageBitmap(cover)
        }

        titleView.text = title
        titleView.isSelected = true
        val hasArtist = !artist.isNullOrEmpty()
        artistView.text = artist
        artistView.isSelected = hasArtist
        artistView.visibility = if (hasArtist) View.VISIBLE else View.GONE

        addView(v)
    }
}
