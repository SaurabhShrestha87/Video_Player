package org.videolan.resources

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.util.VLCUtil
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.resources.VLCInstance.init
import org.videolan.resources.util.VLCCrashHandler
import org.videolan.tools.SingletonHolder

object VLCInstance : SingletonHolder<ILibVLC, Context>({ init(it.applicationContext) }) {
    const val TAG = "VLC/UiTools/VLCInstance"

    @SuppressLint("StaticFieldLeak")
    private lateinit var sLibVLC: ILibVLC

    private val libVLCFactory= FactoryManager.getFactory(ILibVLCFactory.factoryId) as ILibVLCFactory

    @Throws(IllegalStateException::class)
    fun init(ctx: Context) : ILibVLC {
        Thread.setDefaultUncaughtExceptionHandler(VLCCrashHandler())

        if (!VLCUtil.hasCompatibleCPU(ctx)) {
            Log.e(TAG, VLCUtil.getErrorMsg())
            throw IllegalStateException("LibVLC initialisation failed: " + VLCUtil.getErrorMsg())
        }

        // TODO change LibVLC signature to accept a List instead of an ArrayList
        sLibVLC = libVLCFactory.getFromOptions(ctx, VLCOptions.libOptions)
        return sLibVLC
    }

    @Throws(IllegalStateException::class)
    suspend fun restart() = withContext(Dispatchers.IO) {
        sLibVLC.release()
        sLibVLC = libVLCFactory.getFromOptions(AppContextProvider.appContext, VLCOptions.libOptions)
        instance = sLibVLC
        Medialibrary.getInstance().setLibVLCInstance((sLibVLC as LibVLC).getInstance())
    }

    fun testCompatibleCPU(context: Context): Boolean {
        return if (!VLCUtil.hasCompatibleCPU(context)) {
            if (context is Activity) {
                val i = Intent(Intent.ACTION_VIEW).setClassName(context.applicationContext, COMPATERROR_ACTIVITY)
                context.startActivity(i)
            }
            false
        } else true
    }
}
