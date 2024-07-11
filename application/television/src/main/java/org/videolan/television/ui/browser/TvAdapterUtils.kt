package org.videolan.television.ui.browser

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.videolan.libvlc.util.AndroidUtil
import org.videolan.television.ui.FocusableConstraintLayout
import com.video.offline.videoplayer.R

object TvAdapterUtils {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun itemFocusChange(hasFocus: Boolean, itemSize: Int, container: FocusableConstraintLayout, isList: Boolean, listener: () -> Unit) {
        if (hasFocus) {
            val growFactor = if (isList) 1.05 else 1.1
            var newWidth = (itemSize * growFactor).toInt()
            if (newWidth % 2 == 1) {
                newWidth--
            }
            val scale = newWidth.toFloat() / itemSize
            if (AndroidUtil.isLolliPopOrLater)
                container.animate().scaleX(scale).scaleY(scale).translationZ(scale)
            else
                container.animate().scaleX(scale).scaleY(scale)

            listener()
        } else {
            if (AndroidUtil.isLolliPopOrLater)
                container.animate().scaleX(1f).scaleY(1f).translationZ(1f)
            else
                container.animate().scaleX(1f).scaleY(1f)
        }

        (container.background as? TransitionDrawable)?.let {
            if (hasFocus) it.startTransition(250) else it.reverseTransition(250)
        }
    }
}