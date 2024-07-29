
package com.video.offline.videoplayer.gui.privacy.vault.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.video.offline.videoplayer.gui.privacy.vault.utils.Constants;


public class PressableConstraintLayout extends ConstraintLayout {

    public PressableConstraintLayout(Context context) {
        super(context);
    }

    public PressableConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressableConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        setAlpha(pressed ? Constants.HALF : Constants.FULL);
    }
}
