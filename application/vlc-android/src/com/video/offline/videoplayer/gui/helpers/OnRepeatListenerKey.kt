package com.video.offline.videoplayer.gui.helpers

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Lifecycle

/**
 *
 * @param initialInterval Initial interval in millis
 * @param normalInterval Normal interval in millis
 * @param clickListener The OnClickListener to trigger
 */
class OnRepeatListenerKey(private val initialInterval: Int, private val normalInterval: Int, speedUpDelay: Int, private val clickListener: View.OnClickListener, listenerLifecycle: Lifecycle) : View.OnKeyListener, OnRepeatListener(initialInterval, normalInterval, speedUpDelay, clickListener, listenerLifecycle) {

    /**
     *
     * @param clickListener The OnClickListener to trigger
     */
    constructor(clickListener: View.OnClickListener, listenerLifecycle: Lifecycle) : this(DEFAULT_INITIAL_DELAY, DEFAULT_NORMAL_DELAY, DEFAULT_SPEEDUP_DELAY, clickListener, listenerLifecycle)

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (view == null || event == null) return false
        if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
               if (downView != view) startRepeating(view)
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
