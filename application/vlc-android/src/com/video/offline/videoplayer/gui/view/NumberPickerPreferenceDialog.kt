package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.preference.PreferenceDialogFragmentCompat
import com.video.offline.videoplayer.R

class NumberPickerPreferenceDialog : PreferenceDialogFragmentCompat() {
    private lateinit var numberPicker: NumberPicker

    override fun onCreateDialogView(context: Context): View {
        val container = layoutInflater.inflate(R.layout.pref_number_picker, null)
        numberPicker = container.findViewById(R.id.number_picker)
        numberPicker.minValue = NumberPickerPreference.MIN_VALUE
        numberPicker.maxValue = NumberPickerPreference.MAX_VALUE

        return container
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        numberPicker.value = (preference as NumberPickerPreference).getPersistedInt()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            numberPicker.clearFocus()
            val newValue: Int = numberPicker.value
            if (preference.callChangeListener(newValue)) {
                (preference as NumberPickerPreference).doPersistInt(newValue)
                preference.summary
            }
        }
    }

    companion object {
        fun newInstance(key: String): NumberPickerPreferenceDialog {
            val fragment = NumberPickerPreferenceDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}