package com.video.offline.videoplayer.widget.utils

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT

object WidgetSizeUtil {


    fun getWidgetsSize(context: Context, widgetId: Int): Pair<Int, Int> {
        val isPortrait = context.resources.configuration.orientation == ORIENTATION_PORTRAIT
        return getWidgetWidth(context, isPortrait, widgetId) to getWidgetHeight(context, isPortrait, widgetId)
    }

    fun getAppWidgetManager(context: Context) = AppWidgetManager.getInstance(context)

    private fun getWidgetWidth(context: Context, isPortrait: Boolean, widgetId: Int): Int =
            if (isPortrait) {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            } else {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            }

    private fun getWidgetHeight(context: Context, isPortrait: Boolean, widgetId: Int): Int =
            if (isPortrait) {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            } else {
                getWidgetSizeInDp(context, widgetId, AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            }

    private fun getWidgetSizeInDp(context: Context, widgetId: Int, key: String): Int =
            getAppWidgetManager(context).getAppWidgetOptions(widgetId).getInt(key, 0)

    private fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

}