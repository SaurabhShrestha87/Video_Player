package com.video.offline.videoplayer.util

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import org.videolan.tools.Settings
import org.videolan.tools.putSingle
import com.video.offline.videoplayer.gui.dialogs.WidgetMigrationDialog
import com.video.offline.videoplayer.widget.VLCAppWidgetProviderBlack
import com.video.offline.videoplayer.widget.VLCAppWidgetProviderWhite


private const val WIDGET_MIGRATION_KEY = "widget_migration_key"
object WidgetMigration {
    fun launchIfNeeded(context: AppCompatActivity):Boolean {
        val settings = Settings.getInstance(context)
        if (!settings.getBoolean(WIDGET_MIGRATION_KEY, false)) {
            AppWidgetManager.getInstance(context)?.let {manager ->
                if (manager.getAppWidgetIds(ComponentName(context, VLCAppWidgetProviderWhite::class.java)).isNotEmpty() || manager.getAppWidgetIds(ComponentName(context, VLCAppWidgetProviderBlack::class.java)).isNotEmpty()) {
                    val widgetMigrationDialog = WidgetMigrationDialog()
                    widgetMigrationDialog.show(context.supportFragmentManager, "fragment_widget_migration")
                    return true
                }
                val pm: PackageManager = context.application.packageManager
                pm.setComponentEnabledSetting(ComponentName(context.application, VLCAppWidgetProviderBlack::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                pm.setComponentEnabledSetting(ComponentName(context.application, VLCAppWidgetProviderWhite::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            }
            settings.putSingle(WIDGET_MIGRATION_KEY, true)
        }
        return false
    }
}