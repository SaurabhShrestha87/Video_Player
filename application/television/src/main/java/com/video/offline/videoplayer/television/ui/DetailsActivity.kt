
package com.video.offline.videoplayer.television.ui

import android.os.Bundle

import com.video.offline.videoplayer.television.R
import com.video.offline.videoplayer.television.ui.browser.BaseTvActivity

class DetailsActivity : BaseTvActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_details)
    }

    override fun refresh() {}
}
