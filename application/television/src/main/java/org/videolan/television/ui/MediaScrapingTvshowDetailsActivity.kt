package org.videolan.television.ui

import android.os.Bundle

import org.videolan.television.R
import org.videolan.television.ui.browser.BaseTvActivity

class MediaScrapingTvshowDetailsActivity : BaseTvActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moviepedia_tvshow_details)
    }

    override fun refresh() {}
}

const val TV_SHOW_ID = "TV_SHOW_ID"
