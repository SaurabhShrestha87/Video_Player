package com.video.offline.videoplayer.television.ui

import android.os.Bundle

import com.video.offline.videoplayer.television.R
import com.video.offline.videoplayer.television.ui.browser.BaseTvActivity

class MediaScrapingTvshowDetailsActivity : BaseTvActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moviepedia_tvshow_details)
    }

    override fun refresh() {}
}

const val TV_SHOW_ID = "TV_SHOW_ID"
