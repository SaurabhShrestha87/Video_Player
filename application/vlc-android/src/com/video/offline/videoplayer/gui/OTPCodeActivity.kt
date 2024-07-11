package com.video.offline.videoplayer.gui

import android.os.Bundle
import com.video.offline.videoplayer.R


class OTPCodeActivity : BaseActivity() {


    override fun getSnackAnchorView(overAudioPlayer: Boolean) = window.decorView
    override val displayTitle = true
    override var isOTPActivity = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_code_activity)
    }


}



