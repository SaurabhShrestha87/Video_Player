package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class TopCropImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {

        val width = r - l
        val height = b - t

        val matrix = imageMatrix
        val scaleFactorWidth = width.toFloat() / drawable.intrinsicWidth.toFloat()
        val scaleFactorHeight = height.toFloat() / drawable.intrinsicHeight.toFloat()

        val scaleFactor = if (scaleFactorHeight > scaleFactorWidth) {
            scaleFactorHeight
        } else {
            scaleFactorWidth
        }

        matrix.setScale(scaleFactor, scaleFactor, 0f, 0f)
        imageMatrix = matrix

        return super.setFrame(l, t, r, b)
    }
}