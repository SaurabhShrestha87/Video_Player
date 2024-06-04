/*
 * ************************************************************************
 *  VideoBrowserFragment.kt
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

package org.videolan.vlc.gui.video

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import org.videolan.resources.GROUP_VIDEOS_NAME
import org.videolan.resources.KEY_GROUP_VIDEOS
import org.videolan.tools.Settings
import org.videolan.tools.isStarted
import org.videolan.vlc.R
import org.videolan.vlc.gui.BaseFragment
import org.videolan.vlc.gui.ContentActivity
import org.videolan.vlc.gui.SecondaryActivity
import org.videolan.vlc.gui.dialogs.DisplaySettingsDialog
import org.videolan.vlc.gui.dialogs.VIDEO_GROUPING
import org.videolan.vlc.gui.network.MRLPanelDialog
import org.videolan.vlc.gui.network.TAG_
import org.videolan.vlc.interfaces.Filterable
import org.videolan.vlc.util.findCurrentFragment
import org.videolan.vlc.viewmodels.DisplaySettingsViewModel


/**
 * Fragment containing the video viewpager
 *
 */
class VideoBrowserFragment : BaseFragment(), TabLayout.OnTabSelectedListener, Filterable {
    override fun getTitle() = getString(R.string.videos)
    private lateinit var videoPagerAdapter: VideoPagerAdapter
    override val hasTabs = true
    private var tabLayout: TabLayout? = null

    //    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var viewPager: ViewPager2

    private var needToReopenSearch = false
    private var lastQuery = ""

    private var showVideoGroups: String? = null
    private lateinit var settings: SharedPreferences

    private val displaySettingsViewModel: DisplaySettingsViewModel by activityViewModels()
    var videoGridOnlyFavorites: Boolean = false
        set(value) {
            field = value
            updateTabs()
        }
    var playlistOnlyFavorites: Boolean = false
        set(value) {
            field = value
            updateTabs()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings = Settings.getInstance(requireContext())
        tabLayout = requireActivity().findViewById(R.id.sliding_tabs)
        viewPager = view.findViewById(R.id.pager)
        videoPagerAdapter = VideoPagerAdapter(this)
        viewPager.adapter = videoPagerAdapter
    }

    override fun onStart() {
        setupTabLayout()
        super.onStart()
    }

    override fun onStop() {
        unSetTabLayout()
        (viewPager.findCurrentFragment(childFragmentManager) as? BaseFragment)?.stopActionMode()
        super.onStop()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    override fun onTabSelected(tab: TabLayout.Tab) {
        if (tab.position == 0) {
            viewPager.currentItem = tab.position
            val tabTitle = tab.view.findViewById<TextView>(R.id.tab_title)
            tabTitle?.setTextColor(getColor(requireContext(), R.color.white))
            return
        }
        tabLayout?.selectTab(tabLayout!!.getTabAt(0))
        val tabTitle = tab.view.findViewById<TextView>(R.id.tab_title)
        tabTitle?.setTextColor(R.attr.tab_unselected_color)
        when (tab.position) {
            1 -> {
                Toast.makeText(requireContext(), "Network!!", Toast.LENGTH_SHORT).show()
                MRLPanelDialog().show(requireActivity().supportFragmentManager, TAG_)
            }

            2 -> {
                Toast.makeText(requireContext(), "Cleaner!!", Toast.LENGTH_SHORT).show()
                val i = Intent(requireActivity(), SecondaryActivity::class.java)
                i.putExtra("fragment", SecondaryActivity.CLEANER)
                requireActivity().startActivityForResult(i, SecondaryActivity.ACTIVITY_RESULT_SECONDARY)
            }
            3 -> {
                Toast.makeText(requireContext(), "Privacy!!", Toast.LENGTH_SHORT).show()
                val i = Intent(requireActivity(), SecondaryActivity::class.java)
                i.putExtra("fragment", SecondaryActivity.PRIVACY)
                requireActivity().startActivityForResult(
                    i,
                    SecondaryActivity.ACTIVITY_RESULT_SECONDARY
                )
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        val tabTitle = tab.view.findViewById<TextView>(R.id.tab_title)
        when (tab.position) {
            0 -> {
                if (tabTitle.text == getString(R.string.all_videos)) {
                    tabTitle.text = getString(R.string.all_folder)
                    lifecycleScope.launch {
                        displaySettingsViewModel.send(
                            VIDEO_GROUPING, DisplaySettingsDialog.VideoGroup.GROUP_BY_FOLDER
                        )
                    }
                } else if (tabTitle.text == getString(R.string.all_folder)) {
                    tabTitle.text = getString(R.string.all_videos)
                    lifecycleScope.launch {
                        displaySettingsViewModel.send(
                            VIDEO_GROUPING, DisplaySettingsDialog.VideoGroup.GROUP_BY_NAME
                        )
                    }
                }
            }
        }
        Log.d(TAG, "onTabReselected tab.position: ${tab.position}")
        Log.d(TAG, "onTabReselected tabTitle: ${tabTitle.text}")
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        stopActionMode()
        needToReopenSearch = (activity as? ContentActivity)?.isSearchViewVisible() ?: false
        lastQuery = (activity as? ContentActivity)?.getCurrentQuery() ?: ""
        if (isStarted()) (viewPager.findCurrentFragment(childFragmentManager) as? BaseFragment)?.stopActionMode()
        val tabTitle = tab.view.findViewById<TextView>(R.id.tab_title)
        tabTitle?.setTextColor(R.attr.tab_unselected_color)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        reopenSearchIfNeeded()
    }

    /**
     * Reopens the search if the tab is changed
     *
     */
    private fun reopenSearchIfNeeded() {
        if (needToReopenSearch) {
            (activity as? ContentActivity)?.openSearchView()
            (activity as? ContentActivity)?.setCurrentQuery(lastQuery)
            lastQuery = ""
            needToReopenSearch = false
        }
    }

    private fun setupTabLayout() {
        if (tabLayout == null || !::viewPager.isInitialized) return
        tabLayout?.addOnTabSelectedListener(this)
        tabLayout?.let {
            it.addTab(tabLayout!!.newTab())
            it.addTab(tabLayout!!.newTab())
            it.addTab(tabLayout!!.newTab())
            it.addTab(tabLayout!!.newTab())
        }
        updateTabs()
    }

    private fun getPageTitle(position: Int): String {
        val videoGroup = settings.getString(KEY_GROUP_VIDEOS, GROUP_VIDEOS_NAME)
        return when (position) {
            0 -> if (videoGroup == GROUP_VIDEOS_NAME) getString(R.string.all_videos) else getString(
                R.string.all_folder
            )

            1 -> getString(R.string.network)
            2 -> getString(R.string.cleaner)
            3 -> getString(R.string.privacy)
            else -> getString(R.string.error)
        }
    }

    override fun hasFAB(): Boolean {
        return !::viewPager.isInitialized || viewPager.currentItem == 0
    }

    private fun unSetTabLayout() {
        if (tabLayout == null || !::viewPager.isInitialized) return
        tabLayout?.removeOnTabSelectedListener(this)
//        tabLayoutMediator.detach()
    }

    /**
     * View pager adapter hosting the video and playlist fragments
     *
     * @property fa the [FragmentActivity] to be used
     */
    inner class VideoPagerAdapter(fa: VideoBrowserFragment) : FragmentStateAdapter(fa) {

        override fun getItemCount() = 1

        // Returns the fragment to display for that page
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> VideoGridFragment.newInstance()
                else -> throw IllegalStateException("Invalid fragment index")
            }
        }
    }

    /**
     * Finds current fragment
     *
     * @return the current shown fragment
     */
    private fun getCurrentFragment() =
        childFragmentManager.findFragmentByTag("f" + viewPager.currentItem)

    override fun getFilterQuery() = try {
        (getCurrentFragment() as? Filterable)?.getFilterQuery()
    } catch (e: Exception) {
        null
    }

    override fun enableSearchOption() =
        (getCurrentFragment() as? Filterable)?.enableSearchOption() ?: false

    override fun filter(query: String) {
        (getCurrentFragment() as? Filterable)?.filter(query)
    }

    override fun restoreList() {
        (getCurrentFragment() as? Filterable)?.restoreList()
    }

    override fun setSearchVisibility(visible: Boolean) {
        (getCurrentFragment() as? Filterable)?.setSearchVisibility(visible)
    }

    override fun allowedToExpand() =
        (getCurrentFragment() as? Filterable)?.allowedToExpand() ?: false

    private fun updateTabs() {
        for (i in 0 until tabLayout!!.tabCount) {
            val tab = tabLayout!!.getTabAt(i)
            val view = tab?.customView ?: View.inflate(requireActivity(), R.layout.audio_tab, null)
            val title = view.findViewById<TextView>(R.id.tab_title)
            if (tabLayout?.selectedTabPosition == i) {
                title.setTextColor(getColor(requireContext(), R.color.white))
            }
            title.text = getPageTitle(i)
            tab?.customView = view
        }
    }

    companion object {
        private const val TAG = "TAG"
    }

}

