/*****************************************************************************
 * AboutFragment.kt
 *
 * Copyright © 2018 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 */

package org.videolan.vlc.gui

import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.lifecycleScope
import androidx.paging.InitialPagedList
import androidx.paging.PagedList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.KEY_CLEANER_BIG
import org.videolan.resources.KEY_CLEANER_WATCHED
import org.videolan.resources.util.waitForML
import org.videolan.tools.MultiSelectHelper
import org.videolan.tools.setGone
import org.videolan.vlc.R
import org.videolan.vlc.gui.browser.MediaBrowserFragment
import org.videolan.vlc.gui.video.cleaner.CleanerListFragment
import org.videolan.vlc.viewmodels.mobile.VideoGroupingType
import org.videolan.vlc.viewmodels.mobile.VideosViewModel
import org.videolan.vlc.viewmodels.mobile.getViewModel
import java.io.File

class CleanerFragment : MediaBrowserFragment<VideosViewModel>() {

    private lateinit var pagedList: PagedList<MediaLibraryItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.cleaner, container, false)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    override fun getTitle() = "Cleaner"
    override fun getMultiHelper(): MultiSelectHelper<VideosViewModel>? {
        return null
    }

    override fun onRefresh() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel(VideoGroupingType.NONE, null, null)
        setDataObservers()
    }

    private fun setDataObservers() {
        lifecycleScope.launch {
            waitForML()
            viewModel.provider.pagedList.observe(this@CleanerFragment) {
                @Suppress("UNCHECKED_CAST") (it as? PagedList<MediaLibraryItem>)?.let { pagedList ->
                    this@CleanerFragment.pagedList = pagedList
                }
                updateEmptyView()
                if (it !is InitialPagedList<*, *> && activity?.isFinishing == false && viewModel.group != null && it.size < 2 && viewModel.filterQuery.isNullOrEmpty()) requireActivity().finish()
            }
        }
    }

    private fun updateEmptyView() {
        Log.d("TAG", "updateEmptyView: ${pagedList.stream().count()}")
        val watchedList = ArrayList<MediaLibraryItem>()
        val bigFileList = ArrayList<MediaLibraryItem>()
        var watchedFileLength = 0L
        var bigItemFileLength = 0L
        lifecycleScope.launch {
            pagedList.forEach {
                if (it is MediaWrapper) {
                    val itemFileLength =
                        withContext(Dispatchers.IO) { File(Uri.decode(it.location.substring(5))) }.length()
                    // if itemFileLength is greater than 50 MB, add it to bigFileList
                    if (itemFileLength > 50 * 1024 * 1024) {
                        bigFileList.add(it)
                        bigItemFileLength += itemFileLength
                    }
                    if (it.seen > 0L) {
                        watchedList.add(it)
                        watchedFileLength += itemFileLength
                    }
                }
            }
            fillEmptyView(watchedList, bigFileList, watchedFileLength, bigItemFileLength)
        }
    }

    private fun fillEmptyView(
        watchedList: ArrayList<MediaLibraryItem>,
        bigFileList: ArrayList<MediaLibraryItem>,
        watchedFileLength: Long,
        bigItemFileLength: Long
    ) {
        val watchedFilesSize = Formatter.formatFileSize(requireContext(), watchedFileLength)
        val bigFilesSize = Formatter.formatFileSize(requireContext(), bigItemFileLength)
        val totalFilesSize =
            Formatter.formatFileSize(requireContext(), watchedFileLength + bigItemFileLength)

        requireView().findViewById<TextView>(R.id.cleanWatchedDesc).text =
            "${watchedList.size} watched videos can be cleaned"
        requireView().findViewById<TextView>(R.id.cleanWatchedValue).text = watchedFilesSize

        requireView().findViewById<TextView>(R.id.cleanBigDesc).text =
            "${bigFileList.size} videos over 50 MB can be cleaned"

        requireView().findViewById<TextView>(R.id.cleanBigValue).text = bigFilesSize

        requireView().findViewById<TextView>(R.id.cleanBigValue).text = bigFilesSize

        requireView().findViewById<TextView>(R.id.cleanerTitle).text = totalFilesSize

        requireView().findViewById<View>(R.id.cleanerWatchedTv).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_placeholder, CleanerListFragment.newInstance(KEY_CLEANER_WATCHED))?.commit()
        }
        requireView().findViewById<View>(R.id.cleanerSpaceTv).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_placeholder, CleanerListFragment.newInstance(KEY_CLEANER_BIG))?.commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).setGone()
        requireActivity().findViewById<View>(R.id.sliding_tabs).setGone()
        requireView().findViewById<Button>(R.id.searchButton).visibility = View.GONE
    }
}
