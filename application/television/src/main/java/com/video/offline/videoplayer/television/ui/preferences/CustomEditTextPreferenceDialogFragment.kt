package com.video.offline.videoplayer.television.ui.preferences

import android.R
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.preference.EditTextPreferenceDialogFragment

class CustomEditTextPreferenceDialogFragment : EditTextPreferenceDialogFragment() {

    private var customFilters = emptyArray<InputFilter>()
    private var customInputType = InputType.TYPE_NULL

    companion object {
        fun newInstance(key: String?) = CustomEditTextPreferenceDialogFragment().apply {
            arguments = bundleOf(ARG_KEY to key)
        }
    }

    override fun onBindDialogView(view: View) {
        view.findViewById<EditText>(R.id.edit).apply {
            if (customFilters.isNotEmpty()) filters = customFilters
            if (customInputType != InputType.TYPE_NULL) inputType = customInputType
        }
        super.onBindDialogView(view)
    }

    fun setFilters(filters: Array<InputFilter>) {
        this.customFilters = filters
    }

    fun setInputType(inputType: Int) {
        this.customInputType = inputType
    }
}