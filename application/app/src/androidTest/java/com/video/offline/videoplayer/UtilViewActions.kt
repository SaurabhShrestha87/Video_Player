package com.video.offline.videoplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.hamcrest.Matcher

class OrientationChangeAction internal constructor(private val orientation: Int) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return isRoot()
    }

    override fun getDescription(): String {
        return "change orientation to $orientation"
    }

    override fun perform(uiController: UiController, view: View) {
        uiController.loopMainThreadUntilIdle()
        var activity = getActivity(view.context)
        if (activity == null && view is ViewGroup) {
            val c = view.childCount
            var i = 0
            while (i < c && activity == null) {
                activity = getActivity(view.getChildAt(i).context)
                ++i
            }
        }
        activity!!.requestedOrientation = orientation

        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
        if (resumedActivities.isEmpty()) {
            throw RuntimeException("Could not change orientation")
        }
    }

    private fun getActivity(context: Context): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }
}

class ForceClickAction : ViewAction {
    override fun getConstraints() = isEnabled()
    override fun getDescription() = "Force click a view even if 90% condition is not satisfied"

    override fun perform(uiController: UiController?, view: View?) {
        view?.performClick()
    }
}

fun orientationLandscape(): ViewAction {
    return OrientationChangeAction(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}

fun orientationPortrait(): ViewAction {
    return OrientationChangeAction(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
}
