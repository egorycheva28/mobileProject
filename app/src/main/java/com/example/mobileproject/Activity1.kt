package com.example.mobileproject

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.io.ByteArrayOutputStream
import java.io.IOException

class Activity1 : AppCompatActivity(),
    RotateFragment.OnSeekBarChangeListener1, MaskingFragment.OnSeekBarChangeListener3,
    ScaleFragment.OnSeekBarChangeListener2 {
    lateinit var imageView: ImageView
    var nBitmap: Bitmap? = null
    private var imageBitmap2: Bitmap? = null
    private var imageBitmap3: Bitmap? = null
    private var imageBitmap4: Bitmap? = null
    private var isScale = false
    private var isReset = false
    lateinit var canvasContainer: FrameLayout
    lateinit var retouchCanvas: Canvas
    lateinit var touchView: View
    private var isRetouchMode: Boolean = false
    private var mosaicRadius: Int = 10
    private var touchBitmap: Bitmap? = null
    private var path = android.graphics.Path()

    private val paths = mutableListOf<android.graphics.Path>()

//    private lateinit var framelayout: FrameLayout;
//    private var imageBitmap1: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)
        imageView = findViewById(R.id.imageViewFirstFragment)

        canvasContainer = findViewById(R.id.retouchCanvasContainer)

        val imageUri = intent.getStringExtra("imageUri")
        imageView.setImageURI(Uri.parse(imageUri))
        nBitmap = BitmapFactory.decodeFile(imageUri)


        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            nBitmap = bitmap // сохранить bitmap в переменную
            imageBitmap2 = bitmap
            imageBitmap3 = bitmap
            imageBitmap4 = bitmap
            touchBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            retouchCanvas = Canvas(touchBitmap!!)
        } finally {
            inputStream?.close()
        }
        touchView = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                canvas.drawBitmap(touchBitmap!!, 0f, 0f, null)

                for (path in paths) {
                    drawMosaic(path, canvas)
                }
            }

            override fun onTouchEvent(e: MotionEvent): Boolean {
                val touchX = e.x
                val touchY = e.y

                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        path.moveTo(touchX, touchY)
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        path.lineTo(touchX, touchY)
                        applyMosaic(touchX.toInt(), touchY.toInt())
                    }

                    MotionEvent.ACTION_UP -> {
                        paths.add(android.graphics.Path(path))
                        path.reset()
                    }

                    else -> return false
                }
                invalidate()
                return true
            }

            private fun applyMosaic(x: Int, y: Int) {
                val width = touchBitmap!!.width
                val height = touchBitmap!!.height
                val startX = (x - mosaicRadius).coerceAtLeast(0)
                val startY = (y - mosaicRadius).coerceAtLeast(0)
                val endX = (x + mosaicRadius).coerceAtMost(width - 1)
                val endY = (y + mosaicRadius).coerceAtMost(height - 1)

                var redSum = 0
                var greenSum = 0
                var blueSum = 0
                var pixelCount = 0

                for (i in startX..endX) {
                    for (j in startY..endY) {
                        val pixel = touchBitmap!!.getPixel(i, j)
                        redSum += Color.red(pixel)
                        greenSum += Color.green(pixel)
                        blueSum += Color.blue(pixel)
                        pixelCount++
                    }
                }
                if (pixelCount > 0) {
                    val avgRed = (redSum / pixelCount).coerceIn(0, 255)
                    val avgGreen = (greenSum / pixelCount).coerceIn(0, 255)
                    val avgBlue = (blueSum / pixelCount).coerceIn(0, 255)
                    val averageColor = Color.rgb(avgRed, avgGreen, avgBlue)

                    val paintMosaic = Paint().apply {
                        color = averageColor
                        style = Paint.Style.FILL
                    }
                    for (i in startX..endX) {
                        for (j in startY..endY) {
                            retouchCanvas.drawPoint(i.toFloat(), j.toFloat(), paintMosaic)
                        }
                    }
                }
            }

            private fun drawMosaic(path: android.graphics.Path, canvas: Canvas) {
                val paint = Paint()
                paint.style = Paint.Style.STROKE
                paint.color = Color.TRANSPARENT
                paint.strokeWidth = 12f
                canvas.drawPath(path, paint)
            }
        }

        canvasContainer.addView(touchView)

        val buttonRetouch: ImageButton = findViewById(R.id.retouch_button)
        buttonRetouch.setOnClickListener {
            isRetouchMode = !isRetouchMode
            canvasContainer.visibility = if (isRetouchMode) View.VISIBLE else View.GONE

            nBitmap?.let { bitmap ->
                val intent = Intent(this, RetouchActivity::class.java)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                intent.putExtra("imageBitmap", byteArray)
                startActivity(intent)
            }
        }

        val buttonBack: ImageButton = findViewById(R.id.back_button)
        buttonBack.setOnClickListener {
            finish() // для закрытия текущей активности
        }

        val buttonSave: ImageButton = findViewById(R.id.save_button)
        buttonSave.setOnClickListener {
            nBitmap?.let {
                saveImageToGallery(it)
            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
        }

        val buttonBefore: ImageButton = findViewById(R.id.reset_button)
        buttonBefore.setOnClickListener {
            isScale = false
            nBitmap?.let {
                nBitmap = imageBitmap3
                imageView.setImageBitmap(nBitmap)
            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
        }

        val buttonFace: ImageButton = findViewById(R.id.face_button)
        buttonFace.setOnClickListener {
            val faceFragment = FaceFragment()
            val bundle = Bundle().apply {
                putParcelable("imageBitmap", nBitmap)
            }
            faceFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, faceFragment)
                .commit()
        }

        val buttonFilter: ImageButton = findViewById(R.id.filter_button)
        buttonFilter.setOnClickListener {
            val filterFragment = FilterFragment()
            val bundle = Bundle().apply {
                putParcelable("imageBitmap", nBitmap)
            }
            filterFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, filterFragment)
                .commit()
        }

        val buttonRotate = findViewById(R.id.try_to_rotate) as ImageButton
        buttonRotate.setOnClickListener {
            val firstFragment = RotateFragment()
            setNewFragment(firstFragment);
        }

        val buttonRotate90 = findViewById(R.id.rotate_button) as ImageButton
        buttonRotate90.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = rotateImage90(bitmap);
                imageView.setImageBitmap(result)
                nBitmap = result
            } ?: Toast.makeText(this, "No image to mask", Toast.LENGTH_SHORT).show()
        }

        val buttonScale: ImageButton = findViewById(R.id.crop_button)
        buttonScale.setOnClickListener {
            val scaleFragment = ScaleFragment()
            setNewFragment(scaleFragment);
        }

        /*val buttonScale: ImageButton = findViewById(R.id.crop_button)
        buttonScale.setOnClickListener {
            nBitmap?.let { bitmap ->
                val scaleFactor = 0.5f
                val scaledBitmap = trilinearInterpolation(bitmap, scaleFactor)
                imageView.setImageBitmap(scaledBitmap)
                nBitmap = scaledBitmap
            } ?: Toast.makeText(this, "No image to scale", Toast.LENGTH_SHORT).show()
        }*/

        val buttonMasking = findViewById(R.id.unsharp_button) as ImageButton
        buttonMasking.setOnClickListener {
            val maskingFragment = MaskingFragment()
            setNewFragment(maskingFragment);
        }
    }

    override fun onSeekBarValueChange(value: Int) {
        nBitmap = rotateImage(
            imageBitmap2!!,
            value,
            imageBitmap2!!.width.toDouble(),
            imageBitmap2!!.height.toDouble()
        );
        imageView.setImageBitmap(nBitmap)
    }

    override fun onSeekBarValueChangeScale(value: Float) {
        nBitmap = trilinearInterpolation(imageBitmap4!!, value)
        imageView.setImageBitmap(nBitmap)
    }

    override fun onSeekBarValueChange2(progress1: Int, progress2: Int, progress3: Int) {
        nBitmap =
            Masking(imageBitmap2, progress1.toDouble() / 100.0, progress2, progress3)
        imageView.setImageBitmap(nBitmap)
    }

    private fun setNewFragment(fragment: Fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.framelayout, fragment)
            .commit();
    }

    // коэффиценты для билинейной найдены на просторах интернета
    fun trilinearInterpolation(input: Bitmap, scaleFactor: Float): Bitmap {
        //создаём bitmap где будет итоговое изображение
        val output = Bitmap.createBitmap(
            Math.round(input.width * scaleFactor),
            Math.round(input.height * scaleFactor), input.config
        )

        //считаем коэффицент, чтобы был чуть больше исходного
        val scaleFactor2 = (0.5 + scaleFactor / 2).toFloat()

        val secondResult = bilinearInterpolation(
            bilinearInterpolation(input, scaleFactor2),
            scaleFactor / scaleFactor2
        )
        val firstResult = bilinearInterpolation(input, scaleFactor)

        //усредненее цвета из двух утоговых изображений
        for (i in 0 until output.height - 1) {
            for (j in 0 until output.width - 1) {
                if (i < secondResult.height || j < secondResult.width) {
                    val first = firstResult.getPixel(j, i)
                    val second = secondResult.getPixel(j, i)

                    val alpha = (Color.alpha(first) + Color.alpha(second)) / 2
                    val red = (Color.red(first) + Color.red(second)) / 2
                    val green = (Color.green(first) + Color.green(second)) / 2
                    val blue = (Color.blue(first) + Color.blue(second)) / 2

                    output.setPixel(j, i, Color.argb(alpha, red, green, blue))
                } else {
                    output.setPixel(j, i, firstResult.getPixel(j, i))
                }

            }
        }
        return output
    }

    fun bilinearInterpolation(input: Bitmap, scaleFactor: Float): Bitmap {
        //создаём итоговый bitmap
        val newWidth = (input.width * scaleFactor).toInt()
        val newHeight = (input.height * scaleFactor).toInt()
        val output = Bitmap.createBitmap(newWidth, newHeight, input.config)

        //поможет определить какие пиксели исходного изображения должны быть использованы
        // для интерполяции и создания новых пикселей
        val widthRatio = input.width.toFloat() / newWidth
        val heightRatio = input.height.toFloat() / newHeight

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                //вычисляются соответствующие координаты в исходном изображении
                val originX = x * widthRatio
                val originY = y * heightRatio

                //1-координаты верхнего левого, 2- нижнего правого
                val x1 = originX.toInt()
                val y1 = originY.toInt()
                val x2 = if (x1 + 1 < input.width) x1 + 1 else x1
                val y2 = if (y1 + 1 < input.height) y1 + 1 else y1

                //определяют вклад каждого из четырех соседних пикселей исходного изображения
                // в итоговый цвет нового пикселя
                val xPercent = originX - x1
                val yPercent = originY - y1

                val topLeft = input.getPixel(x1, y1)
                val topRight = input.getPixel(x2, y1)
                val bottomLeft = input.getPixel(x1, y2)
                val bottomRight = input.getPixel(x2, y2)

                //высчитываем итоговый цвет
                val firstColor = bilinearInterpolateColor(topLeft, topRight, xPercent)
                val secondColor = bilinearInterpolateColor(bottomLeft, bottomRight, xPercent)
                val resultColor = bilinearInterpolateColor(firstColor, secondColor, yPercent)

                output.setPixel(x, y, resultColor)
            }
        }

        return output
    }


    fun bilinearInterpolateColor(color1: Int, color2: Int, weight: Float): Int {
        val alpha1 = color1 shr 24 and 0xff
        val red1 = color1 shr 16 and 0xff
        val green1 = color1 shr 8 and 0xff
        val blue1 = color1 and 0xff

        val alpha2 = color2 shr 24 and 0xff
        val red2 = color2 shr 16 and 0xff
        val green2 = color2 shr 8 and 0xff
        val blue2 = color2 and 0xff
        val inverseWeight = 1 - weight

        val alpha = (alpha1 * inverseWeight + alpha2 * weight).toInt()
        val red = (red1 * inverseWeight + red2 * weight).toInt()
        val green = (green1 * inverseWeight + green2 * weight).toInt()
        val blue = (blue1 * inverseWeight + blue2 * weight).toInt()

        return alpha shl 24 or (red shl 16) or (green shl 8) or blue
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val resolver = contentResolver
        val fileName = System.currentTimeMillis().toString() + ".png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "Failed to create file for image", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save image: ${e.localizedMessage}", Toast.LENGTH_LONG)
                .show()
            e.printStackTrace()
        }
    }

    private fun rotateImage90(bitmap: Bitmap?): Bitmap {
        val height = bitmap!!.height
        val width = bitmap.width

        var rotatedBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                rotatedBitmap.setPixel(height - y - 1, x, pixel)
            }
        }
        return rotatedBitmap
    }
}