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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.videolan.tools.setGone
import org.videolan.vlc.R
import org.videolan.vlc.gui.video.CleanerListFragment

class CleanerFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.cleaner, container, false)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    override fun getTitle() = "Cleaner"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).setGone()
        requireActivity().findViewById<View>(R.id.sliding_tabs).setGone()
        view.findViewById<View>(R.id.cleanerWatchedTv).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_placeholder, CleanerListFragment.newInstance())?.commit()
        }
        view.findViewById<View>(R.id.cleanerSpaceTv).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_placeholder, CleanerListFragment.newInstance())?.commit()
        }
    }
}
