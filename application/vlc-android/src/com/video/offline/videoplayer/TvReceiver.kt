package com.video.offline.videoplayer

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import org.videolan.libvlc.util.AndroidUtil
import org.videolan.resources.AndroidDevices
import com.video.offline.videoplayer.util.launchChannelUpdate
import com.video.offline.videoplayer.util.setChannel

class TvReceiver : BroadcastReceiver() {

    @TargetApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == null || !AndroidDevices.isAndroidTv) return
        Log.d(TAG, "onReceive: $action")
        when (action) {
            TvContract.ACTION_INITIALIZE_PROGRAMS -> {
                Log.d(TAG, "onReceive: ACTION_INITIALIZE_PROGRAMS ")
                setChannel(context)
            }
            TvContract.ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT -> {
                val previewId = intent.getLongExtra(TvContract.EXTRA_PREVIEW_PROGRAM_ID, -1L)
                val nextId = intent.getLongExtra(TvContract.EXTRA_WATCH_NEXT_PROGRAM_ID, -1L)
                Log.d(TAG, "onReceive: ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT$previewId, $nextId")
            }
            TvContract.ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED -> {
                val previewProgramId = intent.getLongExtra(TvContract.EXTRA_PREVIEW_PROGRAM_ID, -1L)
                val previewInternalId = intent.getLongExtra(TvContract.EXTRA_WATCH_NEXT_PROGRAM_ID, -1L)
                Log.d(TAG, "onReceive: ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED, $previewProgramId, $previewInternalId")
            }
            TvContract.ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED -> {
                val watchNextProgramId = intent.getLongExtra(TvContract.EXTRA_PREVIEW_PROGRAM_ID, -1L)
                val watchNextInternalId = intent.getLongExtra(TvContract.EXTRA_WATCH_NEXT_PROGRAM_ID, -1L)
                Log.d(TAG, "onReceive: ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED, $watchNextProgramId, $watchNextInternalId")
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "onReceive: ACTION_BOOT_COMPLETED ")
                if (!AndroidUtil.isOOrLater)
                    scheduleRecommendationUpdate(context)
                else
                    context.launchChannelUpdate()
                if (AndroidDevices.watchDevices) context.enableStorageMonitoring()
            }
        }
    }

    private fun scheduleRecommendationUpdate(context: Context) {
        val alarmManager = context.applicationContext.getSystemService<AlarmManager>()!!
        val ri = Intent(context, RecommendationsService::class.java)
        val pi = PendingIntent.getService(context, 0, ri,  PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, INITIAL_DELAY, AlarmManager.INTERVAL_HOUR, pi)
    }

    companion object {

        private const val TAG = "VLC/TvReceiver"

        private const val INITIAL_DELAY: Long = 5000
    }
}
