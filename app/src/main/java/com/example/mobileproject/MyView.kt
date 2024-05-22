package com.example.mobileproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.pow
import kotlin.math.sqrt


class MyView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val dotPaint = Paint().apply {
        color = Color.RED // Цвет точек
        style = Paint.Style.FILL
    }

    private val linePaint = Paint().apply {
        color = Color.BLUE // Цвет прямых линий
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val splinePaint = Paint().apply {
        color = Color.GREEN // Цвет сплайнов
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val dotCoordinates = mutableListOf<Pair<Float, Float>>()
    private val buttonHeight = 200 // Высота места под кнопку
    private var drawSpline = false
    private var selectedPointIndex = -1

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val canvasHeight = height - buttonHeight
        if (dotCoordinates.isEmpty()) return
        else if (dotCoordinates.size < 3) {
            // Если точек меньше трех, выводим всплывающее уведомление
            showToast("Нужно минимум три точки для построения сплайна")
        }

        // Рисуем прямые линии между точками
        for (i in 1 until dotCoordinates.size) {
            val (prevX, prevY) = dotCoordinates[i - 1]
            val (currX, currY) = dotCoordinates[i]
            if (prevY <= canvasHeight && currY <= canvasHeight) {
                canvas.drawLine(prevX, prevY, currX, currY, linePaint)
            }
        }

        if (drawSpline && dotCoordinates.size >= 3) {
            // Рисуем сплайн через точки (квадратичные кривые Безье)
            drawQuadraticBezierSpline(canvas)
        }

        // Рисуем точки поверх линий и сплайнов
        for ((x, y) in dotCoordinates) {
            if (y <= canvasHeight) {
                canvas.drawCircle(x, y, 10f, dotPaint)
            }
        }
    }
    private fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val canvasHeight = height - buttonHeight
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                if (y <= canvasHeight) {
                    selectedPointIndex = getPointIndexAt(x, y)
                    if (selectedPointIndex == -1) {
                        // Add points only if spline is not drawn
                        if (!drawSpline) {
                            dotCoordinates.add(Pair(x, y))
                            invalidate() // Redraw the view
                        }
                    } else {
                        performHapticFeedback(MotionEvent.ACTION_DOWN)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (selectedPointIndex != -1) {
                    val x = event.x
                    val y = event.y
                    if (y <= canvasHeight) {
                        dotCoordinates[selectedPointIndex] = Pair(x, y)
                        invalidate() // Redraw the view
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedPointIndex = -1
            }
        }
        return true
    }

    private fun getPointIndexAt(x: Float, y: Float): Int {
        val threshold = 20
        for (i in dotCoordinates.indices) {
            val (px, py) = dotCoordinates[i]
            if (sqrt((px - x).pow(2) + (py - y).pow(2)) <= threshold) {
                return i
            }
        }
        return -1
    }

    // Method to handle long press for deleting points
    override fun performHapticFeedback(feedbackConstant: Int): Boolean {
        postDelayed({
            if (selectedPointIndex != -1) {
                dotCoordinates.removeAt(selectedPointIndex)
                selectedPointIndex = -1
                invalidate()
            }
        }, 2000) // Long press duration

        return super.performHapticFeedback(feedbackConstant)
    }

    // Method to trigger spline drawing
    fun drawSpline() {
        drawSpline = true
        invalidate() // Перерисовываем представление
    }


    fun clearCanvas() {
        dotCoordinates.clear()
        drawSpline = false
        invalidate() // Перерисовываем представление
    }

    private fun drawQuadraticBezierSpline(canvas: Canvas) {
        if (dotCoordinates.size < 3) return

        val points = mutableListOf<Pair<Float, Float>>()

        // Add the first point
        val firstPoint = dotCoordinates.first()
        points.add(firstPoint)

        for (i in 1 until dotCoordinates.size - 1) {
            val p0 = dotCoordinates[i - 1]
            val p1 = dotCoordinates[i]
            val p2 = dotCoordinates[i + 1]

            val midX1 = (p0.first + p1.first) / 2
            val midY1 = (p0.second + p1.second) / 2
            val midX2 = (p1.first + p2.first) / 2
            val midY2 = (p1.second + p2.second) / 2

            for (t in 0..100) {
                val u = t / 100f
                val x = quadraticBezier(midX1, p1.first, midX2, u)
                val y = quadraticBezier(midY1, p1.second, midY2, u)
                points.add(Pair(x, y))
            }
        }

        // Add the last point
        val lastPoint = dotCoordinates.last()
        points.add(lastPoint)

        for (i in 0 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]
            canvas.drawLine(p0.first, p0.second, p1.first, p1.second, splinePaint)
        }
    }

    private fun quadraticBezier(p0: Float, p1: Float, p2: Float, t: Float): Float {
        val u = 1 - t
        return p0 * u.pow(2) + 2 * p1 * u * t + p2 * t.pow(2)
    }
}
