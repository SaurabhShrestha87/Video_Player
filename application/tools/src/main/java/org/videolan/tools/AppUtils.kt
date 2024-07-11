package org.videolan.tools

import android.os.Build
import android.os.Environment
import android.os.StatFs

@Suppress("DEPRECATION")
object AppUtils {

    fun totalMemory(): Long {
        val statFs = StatFs(Environment.getRootDirectory().absolutePath)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            statFs.blockCountLong * statFs.blockSizeLong
        else (statFs.blockCount * statFs.blockSize).toLong()
    }

    fun freeMemory(): Long {
        val statFs = StatFs(Environment.getRootDirectory().absolutePath)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            statFs.availableBlocksLong * statFs.blockSizeLong
        else (statFs.availableBlocks * statFs.blockSize).toLong()
    }
}