package org.videolan.television.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import org.videolan.television.R

class OnboardingActivity : FragmentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)
    }
}