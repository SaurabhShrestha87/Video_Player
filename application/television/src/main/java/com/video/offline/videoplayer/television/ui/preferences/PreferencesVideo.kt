package com.video.offline.videoplayer.television.ui.preferences

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.videolan.resources.VLCInstance
import org.videolan.tools.KEY_PLAYBACK_RATE_VIDEO
import org.videolan.tools.KEY_PLAYBACK_SPEED_PERSIST_VIDEO
import org.videolan.tools.LOCK_USE_SENSOR
import org.videolan.tools.POPUP_FORCE_LEGACY
import org.videolan.tools.SAVE_BRIGHTNESS
import org.videolan.tools.putSingle
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.helpers.restartMediaPlayer

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class PreferencesVideo : BasePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope by MainScope() {

    override fun getXml() = R.xml.preferences_video

    override fun getTitleId() = R.string.video_prefs_category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findPreference<Preference>("secondary_display_category")?.isVisible = false
        findPreference<Preference>("secondary_display_category_summary")?.isVisible = false
        findPreference<Preference>("enable_clone_mode")?.isVisible = false
        findPreference<Preference>(SAVE_BRIGHTNESS)?.isVisible = false
        findPreference<Preference>(POPUP_FORCE_LEGACY)?.isVisible = false
        findPreference<Preference>(LOCK_USE_SENSOR)?.isVisible = false
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null || key == null) return
        when (key) {
            "preferred_resolution" -> {
                launch {
                    VLCInstance.restart()
                    restartMediaPlayer()
                }
            }

            KEY_PLAYBACK_SPEED_PERSIST_VIDEO -> sharedPreferences.putSingle(KEY_PLAYBACK_RATE_VIDEO, 1.0f)
        }
    }
}
