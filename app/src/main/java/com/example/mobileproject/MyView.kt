package com.example.mobileproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MyView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val dotPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.dots) // Цвет точек
        style = Paint.Style.FILL
    }

    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.lines) // Цвет прямых линий
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val splinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.spline_color) // Цвет сплайнов
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val dotCoordinates = mutableListOf<Pair<Float, Float>>()
    private val buttonHeight = 50 // Высота места под кнопку
    private val buttonWidth = 20
    private var drawSpline = false
    private var selectedPointIndex = -1
    private var drawPseudoPolygon = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val canvasHeight = height - buttonHeight
        val canvasWidth = width - buttonWidth
        if (dotCoordinates.isEmpty()) return
        else if (dotCoordinates.size < 3) {
            // Если точек меньше трех, выводим всплывающее уведомление
            showToast("Нужно минимум три точки для построения сплайна")
        }

        // Рисуем прямые линии между точками
        for (i in 1 until dotCoordinates.size) {
            val (prevX, prevY) = dotCoordinates[i - 1]
            val (currX, currY) = dotCoordinates[i]
            if (prevY <= canvasHeight && currY <= canvasHeight && prevY >= buttonHeight && currY >= buttonHeight &&
                prevX <= canvasWidth && currX <= canvasWidth && prevX >= buttonWidth && currX >= buttonWidth) {
                canvas.drawLine(prevX, prevY, currX, currY, linePaint)
                drawAntiAliasedLine(canvas, prevX, prevY, currX, currY, linePaint.color)
            }
        }

        if (drawSpline && dotCoordinates.size >= 3) {
            // Рисуем сплайн через точки (квадратичные кривые Безье)
            drawQuadraticBezierSpline(canvas)
        }

        if (drawPseudoPolygon && dotCoordinates.size >= 3) {
            val (prevX, prevY) = dotCoordinates[0]
            val (currX, currY) = dotCoordinates[dotCoordinates.size - 1]
            canvas.drawLine(prevX, prevY, currX, currY, linePaint)
            drawClosedBezierSpline(canvas)
        }

        // Рисуем точки поверх линий и сплайнов
        for ((x, y) in dotCoordinates) {
            if (y <= canvasHeight && y >= buttonHeight &&
                x <= canvasWidth && x >= buttonWidth) {
                canvas.drawCircle(x, y, 10f, dotPaint)
            }
        }
    }

    private fun drawClosedBezierSpline(canvas: Canvas) {
        if (dotCoordinates.size < 3) return

        val tempPoints = dotCoordinates.toMutableList()

        // Добавляем первую точку в конец временного списка для замыкания сплайна
        tempPoints.add(tempPoints.first())

        val points = mutableListOf<Pair<Float, Float>>()

        for (i in 1 until tempPoints.size - 1) {
            val p0 = tempPoints[i - 1]
            val p1 = tempPoints[i]
            val p2 = tempPoints[i + 1]

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
        val p0 = tempPoints[tempPoints.size - 2]
        val p1 = tempPoints[0]
        val p2 = tempPoints[1]

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

        for (i in 0 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]
            canvas.drawLine(p0.first, p0.second, p1.first, p1.second, splinePaint)
            drawAntiAliasedLine(canvas, p0.first, p0.second, p1.first, p1.second, splinePaint.color)
        }
    }


    private fun drawAntiAliasedLine(
        canvas: Canvas,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        color: Int
    ) {
        val deltaX = abs(x1 - x0)
        val deltaY = abs(y1 - y0)
        val length = maxOf(deltaX, deltaY)

        //шаг изменения координат x и y на каждом пикселе
        val incrementX = (x1 - x0) / length
        val incrementY = (y1 - y0) / length

        var x = x0
        var y = y0

        val step = 1.0f / length
        var alpha = 0.0f

        while (alpha <= 1.0f) {
            val antiAliasColor = applyAlpha(color, 1 - alpha)
            canvas.drawPoint(x, y, Paint().apply { this.color = antiAliasColor })
            x += incrementX
            y += incrementY
            alpha += step
        }
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val alphaChannel = (Color.alpha(color) * alpha).toInt()
        return Color.argb(alphaChannel, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val canvasHeight = height - buttonHeight
        val canvasWidth = width - buttonWidth
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                if (y <= canvasHeight && y >= buttonHeight &&
                    x <= canvasWidth && x >= buttonWidth) {
                    selectedPointIndex = getPointIndexAt(x, y)
                    if (selectedPointIndex == -1) {
                        // добавляем точки если сплайн не работал
                        if (!drawSpline and !drawPseudoPolygon) {
                            dotCoordinates.add(Pair(x, y))
                            invalidate()
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
                    if (y <= canvasHeight && y >= buttonHeight &&
                        x <= canvasWidth && x >= buttonWidth) {
                        dotCoordinates[selectedPointIndex] = Pair(x, y)
                        invalidate()
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                selectedPointIndex = -1
            }
        }
        return true
    }

    //проверка подходит точка или нет
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

    // удаляет точки
    override fun performHapticFeedback(feedbackConstant: Int): Boolean {
        postDelayed({
            if (selectedPointIndex != -1) {
                dotCoordinates.removeAt(selectedPointIndex)
                selectedPointIndex = -1
                invalidate()
            }
        }, 7000)

        return super.performHapticFeedback(feedbackConstant)
    }

    fun drawSpline() {
        if (drawPseudoPolygon == false) {
            drawSpline = true
            invalidate()
        }
    }

    fun clearCanvas() {
        dotCoordinates.clear()
        drawSpline = false
        drawPseudoPolygon = false
        invalidate()
    }

    fun drawPseudoPolygon() {
        if (drawSpline == false) {
            drawPseudoPolygon = true
            invalidate()
        }
    }

    private fun drawQuadraticBezierSpline(canvas: Canvas) {
        if (dotCoordinates.size < 3) return

        val points = mutableListOf<Pair<Float, Float>>()

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

        val lastPoint = dotCoordinates.last()
        points.add(lastPoint)

        for (i in 0 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]
            canvas.drawLine(p0.first, p0.second, p1.first, p1.second, splinePaint)
            drawAntiAliasedLine(canvas, p0.first, p0.second, p1.first, p1.second, splinePaint.color)
        }
    }

    private fun quadraticBezier(p0: Float, p1: Float, p2: Float, t: Float): Float {
        val u = 1 - t
        return p0 * u.pow(2) + 2 * p1 * u * t + p2 * t.pow(2)
    }
}