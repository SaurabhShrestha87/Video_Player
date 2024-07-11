package org.videolan.tools

import android.content.Context
import android.util.LruCache
import androidx.annotation.DrawableRes

/**
 * Cache results from {@link android.content.res.Resources#getIdentifier} which uses reflection to
 * perform lookups by string name. Adjust the max-size as application use increases.
 */
object DrawableCache {
    private const val TAG = "VLC/DrawableCache"
    private val memCache = LruCache<String, Int>(8)

    fun getDrawableFromMemCache(ctx: Context, name: String, @DrawableRes defaultDrawable: Int): Int {
        return getOrPutDrawable(name) { ctx.resources.getDrawableOrDefault(name, ctx.packageName, defaultDrawable) }
    }

    @Synchronized
    private fun getOrPutDrawable(key: String, defaultValue: () -> Int): Int {
        val value = memCache.get(key)
        return if (value == null) {
            val answer = defaultValue()
            memCache.put(key, defaultValue())
            answer
        } else {
            value
        }
    }

    @Synchronized
    fun clear() {
        memCache.evictAll()
    }
}
