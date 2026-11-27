package com.vision.assist

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.View

class OverlayView(c: Context) : View(c) {
    private val main = Handler(Looper.getMainLooper())
    private var boxes: List<Pair<RectF, Float>> = emptyList()
    private var srcW = 1; private var srcH = 1
    private val paint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 4f; color = Color.GREEN }
    companion object { private var inst: OverlayView? = null
        fun push(b: List<Pair<RectF, Float>>, w: Int, h: Int) { inst?.let { it.boxes = b; it.srcW = w; it.srcH = h; it.main.postInvalidate() } }
    }
    init { inst = this }
    override fun onDraw(cv: Canvas) {
        val sx = width.toFloat()/srcW; val sy = height.toFloat()/srcH
        for ((r, _) in boxes) cv.drawRect(r.left*sx, r.top*sy, r.right*sx, r.bottom*sy, paint)
    }
}
