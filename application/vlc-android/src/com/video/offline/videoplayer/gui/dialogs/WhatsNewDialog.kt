package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlinx.coroutines.launch
import org.videolan.tools.KEY_ENABLE_REMOTE_ACCESS
import org.videolan.tools.KEY_SHOW_WHATS_NEW
import org.videolan.tools.Settings
import org.videolan.tools.putSingle
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.DialogWhatsNewBinding
import com.video.offline.videoplayer.gui.preferences.PreferencesActivity

class WhatsNewDialog : VLCBottomSheetDialogFragment() {
    override fun getDefaultState(): Int = STATE_EXPANDED

    override fun needToManageOrientation(): Boolean = false

    private lateinit var binding: DialogWhatsNewBinding


    override fun initialFocusedView(): View = binding.title



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogWhatsNewBinding.inflate(layoutInflater, container, false)
        binding.title.text = getString(R.string.whats_new_title, "3.6")
        binding.webserverMore.setOnClickListener {
            lifecycleScope.launch {
                PreferencesActivity.launchWithPref(requireActivity(), KEY_ENABLE_REMOTE_ACCESS)
                dismiss()
            }
        }
        binding.neverAgain.setOnCheckedChangeListener { _, isChecked ->
            Settings.getInstance(requireActivity()).putSingle(KEY_SHOW_WHATS_NEW, !isChecked)
        }
        return binding.root
    }
}

