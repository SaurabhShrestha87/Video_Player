package com.video.offline.videoplayer.gui.onboarding

import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.fragment.app.Fragment
import com.video.offline.videoplayer.util.isTalkbackIsEnabled

abstract class OnboardingFragment: Fragment() {
    lateinit var onboardingFragmentListener: OnboardingFragmentListener
    abstract fun getDefaultViewForTalkback(): View

    override fun onResume() {
        super.onResume()
        if (requireActivity().isTalkbackIsEnabled()) getDefaultViewForTalkback().sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }
}

interface OnboardingFragmentListener {
    fun onNext()
    fun onDone()
}