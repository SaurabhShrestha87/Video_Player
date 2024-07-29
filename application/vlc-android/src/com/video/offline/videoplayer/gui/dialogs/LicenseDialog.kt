package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import org.videolan.resources.util.parcelable
import com.video.offline.videoplayer.databinding.DialogLicenseBinding
import com.video.offline.videoplayer.gui.LibraryWithLicense
import com.video.offline.videoplayer.util.openLinkIfPossible

const val LICENSE_ITEM = "LICENSE_ITEM"

/**
 * Dialog showing a license text
 */
class LicenseDialog : VLCBottomSheetDialogFragment() {

    private lateinit var licenseItem: LibraryWithLicense
    private lateinit var binding: DialogLicenseBinding

    companion object {

        fun newInstance(libraryWithLicense: LibraryWithLicense): LicenseDialog {
            return LicenseDialog().apply {
                arguments = bundleOf(LICENSE_ITEM to libraryWithLicense)
            }
        }
    }

    override fun getDefaultState(): Int {
        return STATE_EXPANDED
    }

    override fun needToManageOrientation(): Boolean {
        return false
    }

    override fun initialFocusedView(): View = binding.title

    override fun onCreate(savedInstanceState: Bundle?) {
        licenseItem = arguments?.parcelable(LICENSE_ITEM) ?: return
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogLicenseBinding.inflate(layoutInflater, container, false)
        binding.library = licenseItem
        binding.licenseButton.setOnClickListener {
            if (licenseItem.licenseLink.isNotEmpty()) requireActivity().openLinkIfPossible(licenseItem.licenseLink)
        }
        return binding.root
    }
}





