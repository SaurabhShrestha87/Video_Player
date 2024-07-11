package com.video.offline.videoplayer.gui.helpers

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

object TipsUtils {
    private var animationCount = 1

    /**
     * Start and get the horizontal swipe
     * @return the swipe animation
     */
    fun horizontalSwipe(view: View, updateListener:((valueAnimator:ValueAnimator)->Unit)? = null): ObjectAnimator {
        view.translationY = 0F
        view.clearAnimation()
        val slideHorizontalAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0F)
        slideHorizontalAnimator.duration = 1600L
        slideHorizontalAnimator.setFloatValues(0F, 30F, -30F, 0F)
        slideHorizontalAnimator.interpolator = LinearInterpolator()

        updateListener?.let { listener ->
            slideHorizontalAnimator.addUpdateListener { listener.invoke(it) }
        }

        slideHorizontalAnimator.startDelay = 1000L
        slideHorizontalAnimator.doOnEnd { slideHorizontalAnimator.start() }
        slideHorizontalAnimator.start()
        return slideHorizontalAnimator
    }

    /**
     * Start a tap animation on all the tap indcators
     */
    fun startTapAnimation(views: List<View>, long: Boolean = false) {
        animationCount++
        val scale = if (animationCount % 2 == 0) 0.9f else 1f
        val delay = if (animationCount % 2 == 0) 1500L else if (long) 800L else 0L
        views.forEach {
            it.animate().scaleX(scale).scaleY(scale).setDuration(200).setInterpolator(AccelerateDecelerateInterpolator())
                .setStartDelay(delay)
                .withEndAction { startTapAnimation(views, long) }
        }
    }
}