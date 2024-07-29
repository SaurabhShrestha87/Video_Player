package com.video.offline.videoplayer.television.ui.browser

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.VerticalGridPresenter
import com.video.offline.videoplayer.television.ui.CardPresenter
import com.video.offline.videoplayer.interfaces.BrowserFragmentInterface

open class GridFragment : VerticalGridSupportFragment(), BrowserFragmentInterface {

    protected lateinit var adapter: ArrayObjectAdapter
    internal var context: FragmentActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity
        val gridPresenter = VerticalGridPresenter()
        gridPresenter.numberOfColumns = NUM_COLUMNS
        setGridPresenter(gridPresenter)
        adapter = ArrayObjectAdapter(CardPresenter(requireActivity()))
        adapter.clear()
        adapter = adapter
    }

    override fun refresh() {}

    companion object {

        protected const val TAG = "VLC/GridFragment"

        private const val NUM_COLUMNS = 4
    }
}