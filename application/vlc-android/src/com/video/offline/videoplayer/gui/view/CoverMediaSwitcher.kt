
package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.videolan.tools.Settings
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.helpers.setEllipsizeModeByPref
import com.video.offline.videoplayer.util.TextUtils

class CoverMediaSwitcher(context: Context, attrs: AttributeSet) : AudioMediaSwitcher(context, attrs) {

    override fun addMediaView(inflater: LayoutInflater, title: String?, artist: String?, album: String?, cover: Bitmap?, trackInfo: String?) {
        val v = inflater.inflate(R.layout.cover_media_switcher_item, this, false)

        val coverView = v.findViewById<ImageView>(R.id.cover)
        val titleView = v.findViewById<TextView>(R.id.song_title)
        val artistView = v.findViewById<TextView>(R.id.song_subtitle)
        val trackInfoView = v.findViewById<TextView?>(R.id.song_track_info)

        if (cover != null) {
            coverView.setImageBitmap(cover)
        } else {
            coverView.setImageDrawable(ContextCompat.getDrawable(v.context, R.drawable.ic_no_thumbnail_song))
        }

        trackInfoView?.visibility = if (Settings.showAudioTrackInfo) View.VISIBLE else View.GONE

        titleView.setOnClickListener { onTextClicked() }
        artistView.setOnClickListener { onTextClicked() }

        titleView.text = title
        artistView.text = TextUtils.separatedString(artist, album)
        trackInfoView?.text = trackInfo

        setEllipsizeModeByPref(titleView, true)
        if (Settings.listTitleEllipsize == 4) titleView.isSelected = true
        setEllipsizeModeByPref(artistView, true)
        if (Settings.listTitleEllipsize == 4) artistView.isSelected = true
        trackInfoView?.let {
            setEllipsizeModeByPref(it, true)
            if (Settings.listTitleEllipsize == 4) it.isSelected = true
        }

        addView(v)

    }
}
