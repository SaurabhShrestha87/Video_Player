package com.video.offline.videoplayer.gui.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.video.offline.videoplayer.R

class OnboardingNotificationPermissionFragment : OnboardingFragment() {
    private lateinit var titleView: TextView
    override fun getDefaultViewForTalkback() = titleView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_notification_permission, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleView = view.findViewById(R.id.permission_title)
    }

    companion object {
        fun newInstance(): OnboardingNotificationPermissionFragment {
            return OnboardingNotificationPermissionFragment()
        }
    }
}