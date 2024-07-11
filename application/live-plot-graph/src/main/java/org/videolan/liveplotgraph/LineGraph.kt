package org.videolan.liveplotgraph

import android.graphics.Paint
import org.videolan.tools.dp

data class LineGraph(val index: Int, val title: String, val color: Int, val data: HashMap<Long, Float> = HashMap()) {
    val paint: Paint by lazy {
        val p = Paint()
        p.color = color
        p.strokeWidth = 2.dp.toFloat()
        p.isAntiAlias = true
        p.style = Paint.Style.STROKE
        p
    }

    override fun equals(other: Any?): Boolean {
        if (other is LineGraph && other.index == index) return true
        return super.equals(other)
    }
}