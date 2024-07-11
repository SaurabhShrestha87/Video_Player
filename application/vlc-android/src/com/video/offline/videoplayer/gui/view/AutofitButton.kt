package com.video.offline.videoplayer.gui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import com.google.android.material.button.MaterialButton

class AutofitButton : MaterialButton {

    var size = 14

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (width != 0) computeTextSize()

        super.setText(text, type)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        computeTextSize()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        computeTextSize()
    }

    private fun computeTextSize() {
        if (layout.getEllipsisCount(maxLines) > 0 && size > 10) {
            size--
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
        }
    }
}