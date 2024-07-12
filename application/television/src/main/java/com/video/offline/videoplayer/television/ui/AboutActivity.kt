package com.video.offline.videoplayer.television.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import org.videolan.resources.util.applyOverscanMargin
import com.video.offline.videoplayer.television.R
import com.video.offline.videoplayer.gui.helpers.UiTools

class AboutActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        UiTools.fillAboutView(this, window.decorView.rootView)
        applyOverscanMargin(this)
    }
}
