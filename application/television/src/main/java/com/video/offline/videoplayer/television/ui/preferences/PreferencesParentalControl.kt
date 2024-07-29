package com.video.offline.videoplayer.television.ui.preferences

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import org.videolan.tools.KEY_SAFE_MODE
import org.videolan.tools.Settings
import org.videolan.tools.Settings.isPinCodeSet
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.PinCodeActivity
import com.video.offline.videoplayer.gui.PinCodeReason

class PreferencesParentalControl : BasePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {


    override fun getXml() = R.xml.preferences_parental_control

    override fun getTitleId() = R.string.parental_control

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!activity.isPinCodeSet()) {
            val intent = PinCodeActivity.getIntent(activity, PinCodeReason.FIRST_CREATION)
            startActivityForResult(intent, 1)
        }
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == null) return false
        when (preference.key) {
            "modify_pin_code" -> {
                val intent = PinCodeActivity.getIntent(activity, PinCodeReason.MODIFY)
                startActivityForResult(intent, 0)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    Toast.makeText(activity, R.string.pin_code_modified, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null || key == null) return
        when (key) {
            KEY_SAFE_MODE -> {
                Settings.safeMode = sharedPreferences.getBoolean(key, false) && activity.isPinCodeSet()
            }
        }
    }
}
