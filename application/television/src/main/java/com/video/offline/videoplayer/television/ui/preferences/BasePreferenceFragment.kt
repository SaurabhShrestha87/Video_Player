package com.video.offline.videoplayer.television.ui.preferences

import android.app.Fragment
import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragment

import com.video.offline.videoplayer.R

abstract class BasePreferenceFragment : LeanbackPreferenceFragment() {

    protected abstract fun getXml(): Int
    protected abstract fun getTitleId(): Int

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(getXml())
    }

    protected fun loadFragment(fragment: Fragment) {
        activity.fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment)
                .addToBackStack("main")
                .commit()
    }

    protected fun buildPreferenceDialogFragment(preference: Preference): PreferenceDialogFragment? {
        return if (preference is EditTextPreference) {
            CustomEditTextPreferenceDialogFragment.newInstance(preference.getKey()).apply {
                setTargetFragment(this@BasePreferenceFragment, 0)
                show(this@BasePreferenceFragment.fragmentManager, DIALOG_FRAGMENT_TAG)
            }
        } else null
    }

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"
    }
}
