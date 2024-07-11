package com.video.offline.videoplayer.gui.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class CollapsibleLinearLayout : LinearLayout {

    private var locked: Boolean = false
    private var onReadyListener: (() -> Unit)? = null
    var isCollapsed = true
    private var animationUpdateListener: ((Float) -> Unit)? = null
    private var maxHeight: Int = -1
    private val animator by lazy {
        ValueAnimator().apply {
            interpolator = AccelerateInterpolator()
            duration = 300
            addUpdateListener { animator ->
                layoutParams.height = animator.animatedValue as Int
                requestLayout()
                animationUpdateListener?.invoke(animator.animatedFraction)
            }
        }
    }

    fun onReady(listener:() -> Unit) {
        this.onReadyListener = listener
    }

    fun setAnimationUpdateListener(listener: (Float) -> Unit) {
        this.animationUpdateListener = listener
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

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize()
    }

    private fun initialize() {
        if (layoutParams is ConstraintLayout.LayoutParams) throw IllegalStateException("The parent should not be a ConstraintLayout to prevent height issues (when set to 0)")
        viewTreeObserver.addOnGlobalLayoutListener(
                object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        maxHeight = height
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        layoutParams.height = 0
                        requestLayout()
                        onReadyListener?.invoke()
                    }
                })
    }

    fun toggle() {
        if (locked) return
        val fromHeight = if (!isCollapsed) maxHeight else 0
        val toHeight = if (!isCollapsed) 0 else maxHeight
        isCollapsed = !isCollapsed
        animator.setIntValues(fromHeight, toHeight)
        animator.start()
    }

    fun collapse() {
        if (locked) return
        if (!isCollapsed) toggle()
    }

    fun lock() {
        if (isCollapsed) toggle()
        locked = true
    }


}