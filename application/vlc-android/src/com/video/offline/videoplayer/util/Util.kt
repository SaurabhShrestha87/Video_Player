package com.video.offline.videoplayer.util

import android.app.Activity
import android.app.ActivityOptions
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Bundle
import org.videolan.resources.AppContextProvider
import org.videolan.resources.VLCInstance
import org.videolan.tools.CloseableUtils
import org.videolan.tools.runBackground
import org.videolan.tools.runOnMainThread
import com.video.offline.videoplayer.gui.video.VideoPlayerActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object Util {
    const val TAG = "VLC/Util"

    fun readAsset(assetName: String, defaultS: String): String {
        var inputStream: InputStream? = null
        var r: BufferedReader? = null
        try {
            inputStream = AppContextProvider.appResources.assets.open(assetName)
            r = BufferedReader(InputStreamReader(inputStream, "UTF8"))
            val sb = StringBuilder()
            var line: String? = r.readLine()
            if (line != null) {
                sb.append(line)
                line = r.readLine()
                while (line != null) {
                    sb.append('\n')
                    sb.append(line)
                    line = r.readLine()
                }
            }
            return sb.toString()
        } catch (e: IOException) {
            return defaultS
        } finally {
            CloseableUtils.close(inputStream)
            CloseableUtils.close(r)
        }
    }

    fun checkCpuCompatibility(ctx: Context) {
        runBackground(Runnable {
            if (!VLCInstance.testCompatibleCPU(ctx))
                runOnMainThread(Runnable {
                    when (ctx) {
                        is Service -> ctx.stopSelf()
                        is VideoPlayerActivity -> ctx.exit(Activity.RESULT_CANCELED)
                        is Activity -> ctx.finish()
                    }
                })
        })
    }

    fun getFullScreenBundle() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val options = ActivityOptions.makeBasic()
        options.setLaunchBounds(null)
        options.toBundle()
    } else Bundle()

}
