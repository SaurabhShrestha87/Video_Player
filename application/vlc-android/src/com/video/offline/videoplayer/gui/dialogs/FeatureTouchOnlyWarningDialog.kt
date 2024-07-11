package com.video.offline.videoplayer.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.gui.view.SwipeToUnlockView


class FeatureTouchOnlyWarningDialog : FeatureFlagWarningDialog() {

    private lateinit var title: TextView
    private lateinit var warning: TextView
    private lateinit var swipeToEnable: SwipeToUnlockView
    private var titleString: String? = null
    private var warningString: String? = null

    companion object {

        /**
         * Create a new FeatureTouchOnlyWarningDialog
         */
        fun newInstance(listener: () -> Unit): FeatureTouchOnlyWarningDialog {

            return FeatureTouchOnlyWarningDialog().apply {
                this.listener = listener
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
       titleString = getString(R.string.touch_only)
        warningString = getString(R.string.touch_only_description)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_feature_flag_warning, container)
        title = view.findViewById(R.id.title)
        warning = view.findViewById(R.id.generic_warning)
        swipeToEnable = view.findViewById(R.id.swipe_to_enable)
        swipeToEnable.isDPADAllowed = false

        swipeToEnable.setOnStartTouchingListener { isCancelable = false }
        swipeToEnable.setOnStopTouchingListener { isCancelable = true }
        swipeToEnable.setOnUnlockListener {
            dismiss()
            listener.invoke()
        }
        
        
        title.text = titleString
        warning.text = warningString
        return view
    }

    override fun getDefaultState(): Int {
        return STATE_EXPANDED
    }

    override fun initialFocusedView(): View = swipeToEnable

    override fun needToManageOrientation(): Boolean {
        return true
    }
}