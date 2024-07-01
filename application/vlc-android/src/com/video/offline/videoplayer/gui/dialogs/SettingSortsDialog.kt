/*
 * ************************************************************************
 *  SortSettingsDialog.kt
 * *************************************************************************
 * Copyright © 2022 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
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
 * **************************************************************************
 *
 *
 */

package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.resources.GROUP_VIDEOS_FOLDER
import org.videolan.resources.GROUP_VIDEOS_NAME
import org.videolan.resources.GROUP_VIDEOS_NONE
import org.videolan.tools.setGone
import org.videolan.tools.setVisible
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.DialogSortSettingsBinding
import com.video.offline.videoplayer.gui.helpers.UiTools.showPinIfNeeded
import com.video.offline.videoplayer.viewmodels.DisplaySettingsViewModel


/**
 * Dialog showing the display settings for a media list (audio video)
 */
class SettingSortsDialog : DialogFragment() {

    private val TAG = "SortSettingsDialog"

    private var currentSort: Int = -1
    private var currentSortDesc = false
    private var selectedSort: Int = -1
    private var currentVideoGrouping: String = GROUP_VIDEOS_NAME
    private lateinit var binding: DialogSortSettingsBinding

    private val displaySettingsViewModel: DisplaySettingsViewModel by activityViewModels()

    companion object {

        fun newInstance(
            currentSort: Int, currentSortDesc: Boolean, videoGroup: String
        ): SettingSortsDialog {
            return SettingSortsDialog().apply {
                arguments = bundleOf(
                    CURRENT_SORT to currentSort,
                    CURRENT_SORT_DESC to currentSortDesc,
                    VIDEO_GROUPING to videoGroup
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch { if (requireActivity().showPinIfNeeded()) dismiss() }
        super.onCreate(savedInstanceState)
        currentSort = arguments?.getInt(CURRENT_SORT)
            ?: throw IllegalStateException("Current sort should be provided")
        currentSortDesc = arguments?.getBoolean(CURRENT_SORT_DESC)
            ?: throw IllegalStateException("Current sort desc should be provided")
        currentVideoGrouping = arguments?.getString(VIDEO_GROUPING)
            ?: throw IllegalStateException("Current sort desc should be provided")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogSortSettingsBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_dialog)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSortOptions()
    }

    /**
     * Update the views for the sort items
     *
     */
    private fun initSortOptions() {
        Log.d(TAG, "updateSorts: $currentSort")
        binding.sortRg.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.sort_name -> {
                    selectedSort = Medialibrary.SORT_FILENAME
                    binding.optionA.text = "From A to Z"
                    binding.optionB.text = "From Z to A"
                }

                R.id.sort_date -> {
                    selectedSort = Medialibrary.SORT_INSERTIONDATE
                    binding.optionA.text = "From new to old"
                    binding.optionB.text = "From old to new"
                }

                R.id.sort_size -> {
                    selectedSort = Medialibrary.SORT_FILESIZE
                    binding.optionA.text = "From small to big"
                    binding.optionB.text = "From big to small"
                }

                R.id.sort_length -> {
                    selectedSort = Medialibrary.SORT_DURATION
                    binding.optionA.text = "From short to long"
                    binding.optionB.text = "From long to short"
                }
            }
        }

        when (currentVideoGrouping) {
            GROUP_VIDEOS_FOLDER -> {
                binding.sortDate.setGone()
                binding.sortDate.isClickable = false
                binding.sortLength.setGone()
                binding.sortLength.isClickable = false
            }

            else -> {
                binding.sortDate.setVisible()
                binding.sortDate.isClickable = true
                binding.sortLength.setVisible()
                binding.sortLength.isClickable = true
            }
        }

        when (currentSort) {
            Medialibrary.SORT_FILENAME -> binding.sortRg.check(R.id.sort_name)
            Medialibrary.SORT_INSERTIONDATE -> binding.sortRg.check(R.id.sort_date)
            Medialibrary.SORT_FILESIZE -> binding.sortRg.check(R.id.sort_size)
            Medialibrary.SORT_DURATION -> binding.sortRg.check(R.id.sort_length)
        }
        if (currentSortDesc) binding.optionRg.check(R.id.option_b) else binding.optionRg.check(R.id.option_a)

        binding.okay.setOnClickListener {
            lifecycleScope.launch {
                displaySettingsViewModel.send(
                    CURRENT_SORT, Pair(selectedSort, !binding.optionA.isChecked)
                )
            }
            this.dismiss()
        }

        binding.cancel.setOnClickListener {
            this.dismiss()
        }
    }
}





