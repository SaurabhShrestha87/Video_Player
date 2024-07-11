package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.video.offline.videoplayer.databinding.DialogWidgetMigrationBinding

class WidgetMigrationDialog : VLCBottomSheetDialogFragment() {
    override fun getDefaultState(): Int = STATE_EXPANDED

    override fun needToManageOrientation(): Boolean = false

    private lateinit var binding: DialogWidgetMigrationBinding


    override fun initialFocusedView(): View = binding.title



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogWidgetMigrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}

