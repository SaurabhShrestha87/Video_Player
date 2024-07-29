package com.video.offline.videoplayer.television.ui.preferences

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import kotlinx.coroutines.*
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.dialogs.FeatureFlagWarningDialog
import com.video.offline.videoplayer.gui.dialogs.RenameDialog
import com.video.offline.videoplayer.util.FeatureFlag
import com.video.offline.videoplayer.util.FeatureFlagManager

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class PreferencesOptional : BasePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope by MainScope() {
    override fun getXml(): Int {
        return R.xml.preferences_optional
    }


    override fun getTitleId(): Int {
        return R.string.optional_features
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parent = findPreference<PreferenceScreen>("optional_features")
        FeatureFlag.values().forEach { featureFlags ->
            val pref = CheckBoxPreference(activity)
            pref.isChecked = FeatureFlagManager.isEnabled(activity, featureFlags)
            pref.title = getString(featureFlags.title)
            pref.key = featureFlags.getKey()
            parent?.addPreference(pref)
            featureFlags.dependsOn?.let { pref.dependency = it.getKey() }
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


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null || key == null) return
        val enabled = findPreference<CheckBoxPreference>(key)!!.isChecked
        FeatureFlagManager.getByKey(key)?.let { FeatureFlagManager.enable(activity, it, enabled) }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == null) return super.onPreferenceTreeClick(preference)
        FeatureFlagManager.getByKey(preference.key)?.let {
            if (it.warning != null) {
                val currentPreference = findPreference<CheckBoxPreference>(preference.key)!!
                if (!currentPreference.isChecked) return true
                currentPreference.isChecked = false
                val dialog = FeatureFlagWarningDialog.newInstance(it) {
                    currentPreference.isChecked = true
                }
                dialog.show((activity as FragmentActivity).supportFragmentManager, RenameDialog::class.simpleName)
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}
