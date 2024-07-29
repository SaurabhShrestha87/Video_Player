package com.video.offline.videoplayer.car

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.car.app.CarAppService
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.videolan.resources.ANDROID_AUTO_APP_PKG
import com.video.offline.videoplayer.util.AccessControl

@RequiresApi(Build.VERSION_CODES.O)
class VLCCarService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(applicationContext).apply {
                AccessControl.getKeysByPackage(ANDROID_AUTO_APP_PKG).forEach { key ->
                    addAllowedHost(ANDROID_AUTO_APP_PKG, key.replace(":", ""))
                }
            }.build()
        }
    }

    override fun onCreateSession() = SettingsSession()
}

class SettingsSession : Session(), DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this@SettingsSession)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        carContext.onBackPressedDispatcher.addCallback(this@SettingsSession, object : OnBackPressedCallback(true) {
            /**
             * Finish the app when the back button is pressed on the root menu
             */
            override fun handleOnBackPressed() {
                val screenManager = carContext.getCarService(ScreenManager::class.java)
                when {
                    screenManager.stackSize > 1 -> screenManager.pop()
                    else -> carContext.finishCarApp()
                }
            }
        })
    }

    override fun onCreateScreen(intent: Intent) = CarSettingsScreen(carContext)
}
