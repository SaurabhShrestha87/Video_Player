package com.video.offline.videoplayer.gui.dialogs

import android.view.View
import org.videolan.libvlc.Dialog
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.VlcQuestionDialogBinding

class VlcQuestionDialog : VlcDialog<Dialog.QuestionDialog, VlcQuestionDialogBinding>() {

    override val layout: Int
        get() = R.layout.vlc_question_dialog

    fun onAction1(@Suppress("UNUSED_PARAMETER") v: View) {
        vlcDialog.postAction(1)
        dismiss()
    }

    fun onAction2(@Suppress("UNUSED_PARAMETER") v: View) {
        vlcDialog.postAction(2)
        dismiss()
    }
}
