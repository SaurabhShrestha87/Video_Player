package com.video.offline.videoplayer.gui.onboarding

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.video.offline.videoplayer.R


class OnboardingPermissionFragment : OnboardingFragment(), View.OnClickListener {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var permissionTitle: TextView
    private lateinit var permMedia: RelativeLayout
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
        permAllImage.setImageResource(R.drawable.uncheck_circle)
    }

    private fun checkAll() {
        permAllImage.setImageResource(R.drawable.check_circle)
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