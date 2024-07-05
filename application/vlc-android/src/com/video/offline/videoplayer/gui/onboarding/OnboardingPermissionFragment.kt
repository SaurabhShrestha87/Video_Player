/*
 * ************************************************************************
 *  OnboardingPermissionFragment.kt
 * *************************************************************************
 * Copyright © 2021 VLC authors and VideoLAN
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

package com.video.offline.videoplayer.gui.onboarding

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.video.offline.videoplayer.R


class OnboardingPermissionFragment : OnboardingFragment(), View.OnClickListener {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var permissionTitle: TextView
    private lateinit var permMedia: FrameLayout
    private lateinit var permAllImage: ImageView

    private var allFileSelected: Boolean = true
    override fun getDefaultViewForTalkback() = permissionTitle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.onboarding_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionTitle = view.findViewById(R.id.permission_title)
        permMedia = view.findViewById(R.id.permMedia)
        permAllImage = view.findViewById(R.id.permAllImage)
        permMedia.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        when (viewModel.permissionType) {
            PermissionType.ALL -> checkAll()
            PermissionType.MEDIA -> uncheckAll()
            PermissionType.NONE -> uncheckAll()
        }
    }

    private fun uncheckAll() {
        permAllImage.setBackgroundResource(R.drawable.uncheck_circle)
    }

    private fun checkAll() {
        permAllImage.setBackgroundResource(R.drawable.check_circle)
    }

    override fun onClick(view: View) {
        view.animate().scaleX(1F).scaleY(1F)
        when (view) {
            permMedia -> {
                allFileSelected = !allFileSelected
                if (allFileSelected) {
                    viewModel.permissionType = PermissionType.ALL
                    checkAll()
                } else {
                    viewModel.permissionType = PermissionType.MEDIA
                    uncheckAll()
                }
            }
        }
    }


    companion object {
        fun newInstance(): OnboardingPermissionFragment {
            return OnboardingPermissionFragment()
        }
    }
}