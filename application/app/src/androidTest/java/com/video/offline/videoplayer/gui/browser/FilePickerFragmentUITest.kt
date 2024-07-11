package com.video.offline.videoplayer.gui.browser

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.junit.Rule
import org.junit.Test
import com.video.offline.videoplayer.R

class FilePickerFragmentUITest : com.video.offline.videoplayer.BaseUITest() {
    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(FilePickerActivity::class.java)

    lateinit var activity: FilePickerActivity

    override fun beforeTest() {
        activity = activityTestRule.activity
    }

    @Test
    fun whenAtSomeFolder_clickOnHomeIconReturnsBackToRoot() {
        onView(com.video.offline.videoplayer.withRecyclerView(R.id.network_list).atPosition(0)).perform(click())
        onView(com.video.offline.videoplayer.withRecyclerView(R.id.network_list).atPosition(0)).perform(click())

        onView(withId(R.id.network_list)).check(matches(com.video.offline.videoplayer.withCount(greaterThan(2))))

    }
}
