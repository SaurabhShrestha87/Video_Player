
package com.video.offline.videoplayer.gui.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.interfaces.OnEqualizerBarChangeListener


class EqualizerBar : LinearLayout {

    private lateinit var bandValueTextView: TextView
    private lateinit var verticalSeekBar: VerticalSeekBar
    private lateinit var bandTextView: TextView
    private var listener: OnEqualizerBarChangeListener? = null

    override fun setNextFocusLeftId(nextFocusLeftId: Int) {
        super.setNextFocusLeftId(nextFocusLeftId)
        verticalSeekBar.nextFocusLeftId = nextFocusLeftId
    }

    override fun setNextFocusRightId(nextFocusRightId: Int) {
        super.setNextFocusRightId(nextFocusRightId)
        verticalSeekBar.nextFocusRightId = nextFocusRightId
    }

    private val seekListener = object : OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar) {
            listener?.onStartTrackingTouch()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {}

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val value = (progress - RANGE) / PRECISION.toFloat()
            // HACK:    VerticalSeekBar programmatically calls onProgress
            //          fromUser will always be false
            //          So use custom getFromUser() instead of fromUser
            listener?.onProgressChanged(value, isFromUser())
            updateValueText()
        }
    }

    private fun isFromUser() = verticalSeekBar.fromUser


    constructor(context: Context, band: Float) : super(context) {
        init(context, band)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, 0f)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun init(context: Context, band: Float) {
        LayoutInflater.from(context).inflate(R.layout.equalizer_bar, this, true)

        verticalSeekBar = findViewById(R.id.equalizer_seek)
        //Force LTR to fix VerticalSeekBar background problem with RTL layout
        verticalSeekBar.layoutDirection = View.LAYOUT_DIRECTION_LTR
        verticalSeekBar.max = 2 * RANGE
        verticalSeekBar.progress = RANGE
        verticalSeekBar.setOnSeekBarChangeListener(seekListener)
        bandTextView = findViewById(R.id.equalizer_band)
        bandValueTextView = findViewById(R.id.band_value)
        bandTextView.text = if (band < 999.5f)
            (band + 0.5f).toInt().toString() + "Hz"
        else
            (band / 1000.0f + 0.5f).toInt().toString() + "kHz"
        updateValueText()
    }

    private fun updateValueText() {
        val newValue = (verticalSeekBar.progress / 10) - 20
        bandValueTextView.text = if (newValue > 0) "+${newValue}dB" else "${newValue}dB"
    }

    fun setValue(value: Float) {
        verticalSeekBar.progress = (value * PRECISION + RANGE).toInt()
        updateValueText()
    }

    fun getValue() = ((verticalSeekBar.progress - RANGE) / PRECISION.toFloat())

    fun setListener(listener: OnEqualizerBarChangeListener?) {
        this.listener = listener
    }

    fun setProgress(fl: Int) {
        verticalSeekBar.progress = fl
        updateValueText()
    }

    fun getProgress(): Int = verticalSeekBar.progress

    companion object {

        const val PRECISION = 10
        const val RANGE = 20 * PRECISION
    }
}
