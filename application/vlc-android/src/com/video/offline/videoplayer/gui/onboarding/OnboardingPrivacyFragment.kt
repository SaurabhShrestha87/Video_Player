package com.video.offline.videoplayer.gui.onboarding

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.video.offline.videoplayer.R

class OnboardingPrivacyFragment : OnboardingFragment(), View.OnClickListener {

    private lateinit var titleView: TextView
    override fun getDefaultViewForTalkback() = titleView

    private lateinit var privacyCheckRoot: RelativeLayout
    private lateinit var privacyCheckImage: ImageView

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.onboarding_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view.findViewById(R.id.privacy_title)
        titleView.movementMethod = LinkMovementMethod.getInstance()
        privacyCheckRoot = view.findViewById(R.id.privacy_check_root)
        privacyCheckImage = view.findViewById(R.id.privacy_check_img)
        privacyCheckRoot.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        view.animate().scaleX(1F).scaleY(1F)
        when (view) {
            privacyCheckRoot -> {
                viewModel.privacyAccepted = !viewModel.privacyAccepted
            }
        }
        if (viewModel.privacyAccepted) checkAll() else uncheckAll()
    }

    private fun uncheckAll() {
        privacyCheckImage.setImageResource(R.drawable.circle_uncheck)
    }

    private fun checkAll() {
        privacyCheckImage.setImageResource(R.drawable.circle_check)
    }

    companion object {
        fun newInstance() = OnboardingPrivacyFragment()
    }
}
