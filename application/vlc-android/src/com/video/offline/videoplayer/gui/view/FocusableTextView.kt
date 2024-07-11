package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.ContextCompat
import org.videolan.tools.dp
import com.video.offline.videoplayer.R

/**
 * TextView that display a custom focused/pressed state depending on the text bounds
 * It's meant to be used when the TextView width is not set to wrap_content and we want a focus
 * state
 *
 */
open class FocusableTextView : androidx.appcompat.widget.AppCompatTextView {
    val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.greenfocus)
            style = Paint.Style.FILL
            textSize = this@FocusableTextView.textSize
            typeface = this@FocusableTextView.typeface
        }
    }

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    private fun initialize() {
        background = FocusDrawable()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        // do not call super to avoid displaying the ripple
    }

    /**
     * Drawable responsible for drawing a pressed/focused state
     * depending on the inner text size
     */
    inner class FocusDrawable : Drawable() {
        override fun isStateful(): Boolean {
            // always return true
            return true
        }

        @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat"))
        override fun getOpacity(): Int {
            return PixelFormat.OPAQUE
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {}
        override fun setAlpha(i: Int) {}
        var pressed = false
        override fun onStateChange(states: IntArray): Boolean {
            invalidateSelf()
            return true
        }

        override fun draw(canvas: Canvas) {
            // draw only when the view is pressed / focused
            if (state.firstOrNull { it == android.R.attr.state_pressed || it == android.R.attr.state_focused } != null) {
                val textBounds = Rect()
                paint.getTextBounds(text.toString(), 0, text.length, textBounds)
                if (gravity and Gravity.RIGHT != 1)
                    canvas.drawRoundRect(width.toFloat() - (textBounds.width().toFloat() + paddingLeft + paddingRight), 0F, width.toFloat(), bounds.height().toFloat(), 4.dp.toFloat(), 4.dp.toFloat(), paint)
                else
                    canvas.drawRoundRect(textBounds.left.toFloat(), 0F, textBounds.right.toFloat() + paddingLeft + paddingRight, bounds.height().toFloat(), 4.dp.toFloat(), 4.dp.toFloat(), paint)
            }
        }
    }
}