package com.example.mobileproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class RetouchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var bitmap: Bitmap? = null
    private var brushSize: Int = 20
    private var retouchFactor: Int = 10
    private val paint = Paint()
    var isRetouch: Boolean = false

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()
    }

    fun setBrushSize(size: Int) {
        brushSize = size
    }

    fun setRetouchFactor(factor: Int) {
        retouchFactor = factor
    }

    init {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isRetouch) return false
        bitmap?.let { bmp ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                applyRetouch(x, y)
                invalidate()
            }
        }
        return true
    }

    private fun applyRetouch(x: Int, y: Int) {
        bitmap?.let { bmp ->
            val w = bmp.width
            val h = bmp.height
            for (i in (x - brushSize)..(x + brushSize)) {
                for (j in (y - brushSize)..(y + brushSize)) {
                    if (i in 0 until w && j in 0 until h && isPointInCircle(i, j, x, y, brushSize)) {
                        val avgColor = calculateAverageColor(bmp, i, j, retouchFactor)
                        bmp.setPixel(i, j, avgColor)
                    }
                }
            }
        }
    }

    private fun isPointInCircle(px: Int, py: Int, cx: Int, cy: Int, radius: Int): Boolean {
        val dx = px - cx
        val dy = py - cy
        return (dx * dx + dy * dy) <= (radius * radius)
    }

    private fun calculateAverageColor(bmp: Bitmap, cx: Int, cy: Int, factor: Int): Int {
        val w = bmp.width
        val h = bmp.height
        val startX = max(0, cx - factor)
        val startY = max(0, cy - factor)
        val endX = min(w - 1, cx + factor)
        val endY = min(h - 1, cy + factor)

        var sumR = 0
        var sumG = 0
        var sumB = 0
        var count = 0

        for (i in startX..endX) {
            for (j in startY..endY) {
                val color = bmp.getPixel(i, j)
                sumR += Color.red(color)
                sumG += Color.green(color)
                sumB += Color.blue(color)
                count++
            }
        }
        return Color.rgb(sumR / count, sumG / count, sumB / count)
    }
}
