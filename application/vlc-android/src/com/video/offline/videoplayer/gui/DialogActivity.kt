package com.video.offline.videoplayer.gui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import org.videolan.libvlc.Dialog
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.resources.util.parcelable
import org.videolan.resources.util.parcelableList
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.dialogs.DeviceDialog
import com.video.offline.videoplayer.gui.dialogs.NetworkServerDialog
import com.video.offline.videoplayer.media.MediaUtils
import com.video.offline.videoplayer.util.showVlcDialog

class DialogActivity : BaseActivity() {
    override fun getSnackAnchorView(overAudioPlayer:Boolean): View? = findViewById<View>(android.R.id.content)
    private var preventFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transparent)
        val key = intent.action
        if (key.isNullOrEmpty()) {
            finish()
            return
        }
        when (key) {
            KEY_SERVER -> setupServerDialog()
            KEY_SUBS_DL -> setupSubsDialog()
            KEY_DEVICE -> setupDeviceDialog()
            KEY_DIALOG -> {
                dialog?.run {
                    showVlcDialog(this)
                    loginDialogShown.postValue(true)
                    dialog = null
                } ?: finish()
            }
            else -> finish()
        }
    }

    private fun setupDeviceDialog() {
        window.decorView.alpha = 0f
        val dialog = DeviceDialog()
        val intent = intent
        dialog.setDevice(intent.getStringExtra(EXTRA_PATH)!!, intent.getStringExtra(EXTRA_UUID)!!, intent.getBooleanExtra(EXTRA_SCAN, false))
        dialog.show(supportFragmentManager, "device_dialog")
    }


    private fun setupServerDialog() {
        val networkServerDialog = NetworkServerDialog()
        intent.parcelable<MediaWrapper>(EXTRA_MEDIA)?.let {
            networkServerDialog.setServer(it)
        }
        networkServerDialog.show(supportFragmentManager, "fragment_edit_network")
    }

    private fun setupSubsDialog() {
        val medialist = intent.parcelableList<MediaWrapper>(EXTRA_MEDIALIST)
        if (medialist != null)
            MediaUtils.getSubs(this, medialist)
        else
            finish()
    }

    override fun finish() {
        loginDialogShown.postValue(false)
        if (preventFinish) {
            preventFinish = false
            return
        }
        super.finish()
    }

    fun preventFinish() {
        preventFinish = true
    }

    companion object {

        var dialog : Dialog? = null
        var loginDialogShown = MutableLiveData(false)
        const val KEY_SERVER = "serverDialog"
        const val KEY_SUBS_DL = "subsdlDialog"
        const val KEY_DEVICE = "deviceDialog"
        const val KEY_DIALOG = "vlcDialog"

        const val EXTRA_MEDIALIST = "extra_media_list"
        const val EXTRA_MEDIA = "extra_media"
        const val EXTRA_PATH = "extra_path"
        const val EXTRA_UUID = "extra_uuid"
        const val EXTRA_SCAN = "extra_scan"
    }
}
