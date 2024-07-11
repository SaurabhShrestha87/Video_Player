package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.video.offline.videoplayer.R

class NumberPickerPreference(context: Context, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    override fun getSummary(): CharSequence {
        return context.getString(R.string.jump_delay_summary, getPersistedInt().toString())
    }

    fun getPersistedInt() = super.getPersistedInt(FALLBACK_DEFAULT_VALUE)

    fun doPersistInt(value: Int) {
        super.persistInt(value)
        notifyChanged()
    }

    /**
     * Saves the text to the current data storage.
     *
     * @param text The text to save
     */
    fun setValue(value:Int) {
        val wasBlocking = shouldDisableDependents()
        persistInt(value)
        val isBlocking = shouldDisableDependents()
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking)
        }
        notifyChanged()
    }

    override fun onSetInitialValue(restore: Boolean, defaultValue: Any?) {
        onSetInitialValue(defaultValue)
        setValue(if (restore) getPersistedInt(FALLBACK_DEFAULT_VALUE) else defaultValue as Int)
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInteger(index, FALLBACK_DEFAULT_VALUE)
    }

    companion object {
        const val FALLBACK_DEFAULT_VALUE = -1
        const val MIN_VALUE = 1
        const val MAX_VALUE = 100
    }
}