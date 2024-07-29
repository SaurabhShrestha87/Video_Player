package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.video.offline.videoplayer.BuildConfig
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.DialogAboutVersionBinding

/**
 * Dialog showing the info of the current version
 */
class AboutVersionDialog : VLCBottomSheetDialogFragment() {

    private lateinit var binding: DialogAboutVersionBinding

    companion object {

        fun newInstance(): AboutVersionDialog {
            return AboutVersionDialog()
        }
    }

    override fun getDefaultState(): Int {
        return STATE_EXPANDED
    }

    override fun needToManageOrientation(): Boolean {
        return false
    }

    override fun initialFocusedView(): View = binding.medias2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DialogAboutVersionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.version.text = BuildConfig.VLC_VERSION_NAME
        binding.medias2.text = getString(R.string.build_time)
        binding.changelog.text = getString(R.string.changelog).replace("*", "•")
        binding.revision.text = getString(R.string.build_revision)
        binding.vlcRevision.text = getString(R.string.build_vlc_revision)
        binding.libvlcRevision.text = getString(R.string.build_libvlc_revision)
        binding.libvlcVersion.text = BuildConfig.LIBVLC_VERSION
        binding.compiledBy.text = getString(R.string.build_host)
        binding.moreButton.setOnClickListener {
            val whatsNewDialog = WhatsNewDialog()
            whatsNewDialog.show(requireActivity().supportFragmentManager, "fragment_whats_new")
            dismiss()
        }
    }


}





