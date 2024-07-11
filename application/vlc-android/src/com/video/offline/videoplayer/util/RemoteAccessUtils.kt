package com.video.offline.videoplayer.util

import kotlinx.coroutines.flow.MutableStateFlow

object RemoteAccessUtils {
    val otpFlow = MutableStateFlow<String?>(null)

}