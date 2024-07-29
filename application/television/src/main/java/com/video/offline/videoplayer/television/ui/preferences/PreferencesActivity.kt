package com.video.offline.videoplayer.television.ui.preferences

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import com.video.offline.videoplayer.television.R
import com.video.offline.videoplayer.television.ui.browser.BaseTvActivity
import org.videolan.tools.KEY_RESTRICT_SETTINGS
import org.videolan.tools.RESULT_RESTART
import org.videolan.tools.RESULT_RESTART_APP
import org.videolan.tools.Settings
import com.video.offline.videoplayer.PlaybackService
import com.video.offline.videoplayer.gui.PinCodeActivity
import com.video.offline.videoplayer.gui.PinCodeReason

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class PreferencesActivity : BaseTvActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.tv_preferences_activity)
        if (Settings.getInstance(this).getBoolean(KEY_RESTRICT_SETTINGS, false)) {
            val intent = PinCodeActivity.getIntent(this, PinCodeReason.CHECK)
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun refresh() {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (!fragmentManager.popBackStackImmediate()) finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyTheme() {
        val pref = Settings.getInstance(this)
        val enableBlackTheme = pref.getBoolean("enable_black_theme", false)
        if (enableBlackTheme) {
            setTheme(R.style.Theme_VLC_Black)
        }
    }

    fun setRestart() {
        setResult(RESULT_RESTART)
    }

    fun setRestartApp() {
        setResult(RESULT_RESTART_APP)
    }

    fun exitAndRescan() {
        setRestart()
        val intent = intent
        finish()
        startActivity(intent)
    }

    fun detectHeadset(detect: Boolean) {
        val le = PlaybackService.headSetDetection
        if (le.hasObservers()) le.value = detect
    }
}
