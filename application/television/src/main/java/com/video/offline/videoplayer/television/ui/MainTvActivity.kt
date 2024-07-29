
package com.video.offline.videoplayer.television.ui

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import org.videolan.medialibrary.interfaces.Medialibrary
import com.video.offline.videoplayer.television.R
import com.video.offline.videoplayer.television.ui.browser.BaseTvActivity
import org.videolan.tools.RESULT_RESCAN
import org.videolan.tools.RESULT_RESTART
import org.videolan.tools.RESULT_RESTART_APP
import com.video.offline.videoplayer.ScanProgress
import com.video.offline.videoplayer.StartActivity
import com.video.offline.videoplayer.gui.helpers.hf.StoragePermissionsDelegate
import com.video.offline.videoplayer.reloadLibrary
import com.video.offline.videoplayer.util.LifecycleAwareScheduler
import com.video.offline.videoplayer.util.SchedulerCallback
import com.video.offline.videoplayer.util.Util

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class MainTvActivity : BaseTvActivity(), StoragePermissionsDelegate.CustomActionController, SchedulerCallback {

    private lateinit var browseFragment: MainTvFragment
    private lateinit var progressBar: ProgressBar
    lateinit var scheduler: LifecycleAwareScheduler


    override fun onTaskTriggered(id: String, data: Bundle) {
        when (id) {
            SHOW_LOADING -> progressBar.visibility = View.VISIBLE
            HIDE_LOADING -> {
                scheduler.cancelAction(SHOW_LOADING)
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduler =  LifecycleAwareScheduler(this)

        Util.checkCpuCompatibility(this)

        setContentView(R.layout.tv_main)

        val fragmentManager = supportFragmentManager
        browseFragment = fragmentManager.findFragmentById(R.id.browse_fragment) as MainTvFragment
        progressBar = findViewById(R.id.tv_main_progress)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_RESULT_PREFERENCES) {
            when (resultCode) {
                RESULT_RESCAN -> this.reloadLibrary()
                RESULT_RESTART, RESULT_RESTART_APP -> {
                    val intent = Intent(this, if (resultCode == RESULT_RESTART_APP) StartActivity::class.java else MainTvActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
            browseFragment.showDetails()
        } else super.onKeyDown(keyCode, event)
    }

    override fun onParsingServiceStarted() {
        scheduler.startAction(SHOW_LOADING)
    }

    override fun onParsingServiceProgress(scanProgress: ScanProgress?) {
        if (progressBar.visibility == View.GONE && Medialibrary.getInstance().isWorking)
            scheduler.startAction(SHOW_LOADING)
    }

    override fun onParsingServiceFinished() {
        if (!Medialibrary.getInstance().isWorking)
            scheduler.scheduleAction(HIDE_LOADING, 500)
    }

    fun hideLoading() {
        scheduler.scheduleAction(HIDE_LOADING, 500)
    }

    override fun onStorageAccessGranted() {
        refresh()
    }

    override fun refresh() {
        this.reloadLibrary()
    }

    companion object {

        const val ACTIVITY_RESULT_PREFERENCES = 1

        const val BROWSER_TYPE = "browser_type"

        const val TAG = "VLC/MainTvActivity"
        private const val SHOW_LOADING = "show_loading"
        private const val HIDE_LOADING = "hide_loading"
    }
}
