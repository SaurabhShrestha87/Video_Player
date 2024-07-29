
package com.video.offline.videoplayer.gui.video.benchmark

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

import com.video.offline.videoplayer.gui.video.VideoPlayerActivity


/**
 * Class to store the overriden methods in BenchActivity
 * for code readability
 */
open class ShallowVideoPlayer : VideoPlayerActivity() {
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onTrackballEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }


    override fun onAudioSubClick(anchor: View?) {}

    override fun onClick(v: View) {}
}
