package com.video.offline.videoplayer.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import org.videolan.tools.KEY_LAST_WHATS_NEW
import org.videolan.tools.KEY_SHOW_WHATS_NEW
import org.videolan.tools.Settings
import org.videolan.tools.putSingle
import com.video.offline.videoplayer.gui.dialogs.WhatsNewDialog

object WhatsNewManager {
    fun launchIfNeeded(context: AppCompatActivity) {
        val preferences = Settings.getInstance(context)
        val needed = preferences.getBoolean(KEY_SHOW_WHATS_NEW, true) && preferences.getString(KEY_LAST_WHATS_NEW, "") != "3.6"
        if (needed) {
            markAsShown(preferences)
            val whatsNewDialog = WhatsNewDialog()
            whatsNewDialog.show(context.supportFragmentManager, "fragment_whats_new")
        }
    }

    fun markAsShown(preferences: SharedPreferences) {
        preferences.putSingle(KEY_LAST_WHATS_NEW, "3.6")
    }
}