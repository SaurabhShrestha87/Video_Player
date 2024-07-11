package com.video.offline.videoplayer.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import org.videolan.tools.Settings
import org.videolan.tools.putSingle
import java.util.*

/**
 * This manager allows the user to enable / disable experimental features
 */
object FeatureFlagManager {

    fun isEnabled(context: Context, feature:FeatureFlag) = Settings.getInstance(context).getBoolean(feature.getKey(), false)
    fun enable(context: Context, feature:FeatureFlag, enabled:Boolean) = Settings.getInstance(context).putSingle(feature.getKey(), enabled)
    fun getByKey(key:String):FeatureFlag? = FeatureFlag.values().firstOrNull { it.getKey() == key }

}

/**
 * An experimental feature that can be disabled
 *
 * @param dependsOn: another feature this feature depends on
 * @param title: a string reference for this feature's title
 */
enum class FeatureFlag(var dependsOn:FeatureFlag?, @StringRes var title:Int, @StringRes var warning:Int?) {

    ;

    @SuppressLint("DefaultLocale")
    fun getKey() = "ff_${name.lowercase(Locale.getDefault())}"
}