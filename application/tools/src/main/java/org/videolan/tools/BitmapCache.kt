package org.videolan.tools

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import videolan.org.commontools.BuildConfig

object BitmapCache {
    private const val TAG = "VLC/BitmapCache"
    private val memCache: LruCache<String, Bitmap>

    init {

        // Use 20% of the available memory for this memory cache.
        val cacheSize = Runtime.getRuntime().maxMemory() / 5

        if (BuildConfig.DEBUG)
            Log.i(TAG, "LRUCache size set to " + cacheSize.readableSize())

        memCache = object : LruCache<String, Bitmap>(cacheSize.toInt()) {

            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.rowBytes * value.height
            }
        }
    }

    @Synchronized
    fun getBitmapFromMemCache(key: String?): Bitmap? {
        if (key == null) return null
        val b = memCache.get(key)
        if (b == null) {
            memCache.remove(key)
            return null
        }
        return b
    }

    @Synchronized
    fun addBitmapToMemCache(key: String?, bitmap: Bitmap?) {
        if (key != null && bitmap != null && getBitmapFromMemCache(key) == null) {
            memCache.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(resId: Int): Bitmap? {
        return getBitmapFromMemCache("res:$resId")
    }

    private fun addBitmapToMemCache(resId: Int, bitmap: Bitmap?) {
        addBitmapToMemCache("res:$resId", bitmap)
    }

    @Synchronized
    fun clear() {
        memCache.evictAll()
    }
}
