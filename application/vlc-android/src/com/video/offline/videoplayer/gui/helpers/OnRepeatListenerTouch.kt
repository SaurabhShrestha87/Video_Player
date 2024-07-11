package com.video.offline.videoplayer.gui.helpers

import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Lifecycle

/**
 *
 * @param initialInterval Initial interval in millis
 * @param normalInterval Normal interval in millis
 * @param clickListener The OnClickListener to trigger
 */
class OnRepeatListenerTouch(private val initialInterval: Int, private val normalInterval: Int, speedUpDelay: Int, private val clickListener: View.OnClickListener, listenerLifecycle: Lifecycle) : View.OnTouchListener, OnRepeatListener(initialInterval, normalInterval, speedUpDelay, clickListener, listenerLifecycle) {
    /**
     *
     * @param clickListener The OnClickListener to trigger
     */
    constructor(clickListener: View.OnClickListener, listenerLifecycle: Lifecycle) : this(DEFAULT_INITIAL_DELAY, DEFAULT_NORMAL_DELAY, DEFAULT_SPEEDUP_DELAY, clickListener, listenerLifecycle)

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startRepeating(view)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopRepeating(view)
                return true
            }
        }
        return false
    }
}
