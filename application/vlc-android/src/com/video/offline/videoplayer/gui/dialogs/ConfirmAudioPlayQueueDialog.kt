package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.video.offline.videoplayer.R


class ConfirmAudioPlayQueueDialog : VLCBottomSheetDialogFragment() {

    private lateinit var listener: () -> Unit
    private lateinit var title: TextView
    private lateinit var acceptButton: Button
    private lateinit var cancelButton: Button


    fun setListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_confirm_audio_playqueue, container)
        title = view.findViewById(R.id.title)
        acceptButton = view.findViewById(R.id.accept_button)
        cancelButton = view.findViewById(R.id.cancel_button)
        acceptButton.setOnClickListener {
            listener.invoke()
            dismiss()
        }
        cancelButton.setOnClickListener { dismiss() }
        return view
    }

    override fun getDefaultState(): Int {
        return STATE_EXPANDED
    }

    override fun initialFocusedView(): View = title

    override fun needToManageOrientation(): Boolean {
        return true
    }
}