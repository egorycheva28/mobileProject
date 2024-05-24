//package com.example.mobileproject
//
//import android.content.ContentValues
//import android.content.Context
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Bundle
//import android.widget.ImageButton
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.widget.Toast
//import org.opencv.android.Utils
//import org.opencv.core.Mat
//import org.opencv.core.MatOfRect
//import org.opencv.core.Rect
//import org.opencv.core.Scalar
//import org.opencv.imgproc.Imgproc
//import org.opencv.objdetect.CascadeClassifier
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.InputStream
//import java.io.OutputStream
//
//
//class Activity1 : AppCompatActivity() {
//    lateinit var imageView: ImageView
//    var nBitmap: Bitmap? = null
//    var nBitmapOriginal: Bitmap? = null
//    private lateinit var retouchImageView: RetouchImageView
//    private var isRetouchMode: Boolean = false
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity1)
//        imageView = findViewById(R.id.imageViewFirstFragment)
//
//        val imageUri = intent.getStringExtra("imageUri") ?: return
//        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
//        try {
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            imageView.setImageBitmap(bitmap)
//            nBitmap = bitmap // сохранить bitmap в переменную
//            nBitmapOriginal = nBitmap
//        } finally {
//            inputStream?.close()
//        }
//
//        val buttonBack: ImageButton = findViewById(R.id.back_button)
//        buttonBack.setOnClickListener {
//            finish() // для закрытия текущей активности
//        }
//
//        val buttonReset: ImageButton = findViewById(R.id.reset_button)
//        buttonReset.setOnClickListener {
//            nBitmap?.let {
//                nBitmap = nBitmapOriginal
//                imageView.setImageBitmap(nBitmap)
//            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
//        }
//
//        val buttonSave: ImageButton = findViewById(R.id.save_button)
//        buttonSave.setOnClickListener {
//            nBitmap?.let {
//                saveImageToGallery(it)
//            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
//        }
//
//        val buttonRotate: ImageButton = findViewById(R.id.rotate_button)
//        buttonRotate.setOnClickListener {
//            nBitmap?.let { bitmap ->
//                val rotatedBitmap = rotateImage(bitmap)
//                imageView.setImageBitmap(rotatedBitmap)
//                nBitmap = rotatedBitmap
//            } ?: Toast.makeText(this, "No image to rotate", Toast.LENGTH_SHORT).show()
//        }
//
//        val buttonScale: ImageButton = findViewById(R.id.crop_button)
//        buttonScale.setOnClickListener {
//            nBitmap?.let { bitmap ->
//                val scaleFactor = 0.8f
//                val scaledBitmap = scaleImage(bitmap, scaleFactor)
//                imageView.setImageBitmap(scaledBitmap)
//                nBitmap = scaledBitmap
//            } ?: Toast.makeText(this, "No image to crop", Toast.LENGTH_SHORT).show()
//        }
//
//        val buttonFace: ImageButton = findViewById(R.id.face_button)
//        buttonFace.setOnClickListener {
//            val faceFragment = FaceFragment()
//            val bundle = Bundle().apply {
//                putParcelable("imageBitmap", nBitmap)
//            }
//            faceFragment.arguments = bundle
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.framelayout, faceFragment)
//                .commit()
//        }
////
////        retouchImageView = findViewById(R.id.retouchImageView)  // Убедитесь, что вы настроили правильный ID в layout
////        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.your_image)
////        retouchImageView.setBitmap(bitmap)
////
////        retouchImageView.setBrushSize(30)
////        retouchImageView.setRetouchFactor(10)
//
//        // Инициализация кнопки ретуширования
////        val buttonRetouch: ImageButton = findViewById(R.id.retouch_button)
////        buttonRetouch.setOnClickListener {
////            isRetouchMode = !isRetouchMode
////            retouchImageView.isRetouchEnabled = isRetouchMode
////            val message = if (isRetouchMode) "Retouch mode enabled" else "Retouch mode disabled"
////            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
////        }
//
//        val buttonRetouch: ImageButton = findViewById(R.id.retouch_button)
//        buttonRetouch.setOnClickListener {
//            val retouchFragment = FilterFragment()
//            val bundle = Bundle().apply {
//                putParcelable("imageBitmap", nBitmap)
//            }
//            retouchFragment.arguments = bundle
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.framelayout, retouchFragment)
//                .commit()
//        }
//
//        val buttonFilter: ImageButton = findViewById(R.id.filter_button)
//        buttonFilter.setOnClickListener {
//            val filterFragment = FilterFragment()
//            val bundle = Bundle().apply {
//                putParcelable("imageBitmap", nBitmap)
//            }
//            filterFragment.arguments = bundle
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.framelayout, filterFragment)
//                .commit()
//        }
//
//        val buttonRotate1: ImageButton = findViewById(R.id.rotate_button)
//        buttonRotate1.setOnClickListener {
//            val rotateFragment = RotateFragment()
//            val bundle = Bundle().apply {
//                putParcelable("imageBitmap", nBitmap)
//            }
//            rotateFragment.arguments = bundle
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.framelayout, rotateFragment)
//                .commit()
//        }
//
//        val buttonMaska: ImageButton = findViewById(R.id.unsharp_button)
//        buttonMaska.setOnClickListener {
//            nBitmap?.let { bitmap ->
//                val result = maska(bitmap);
//                imageView.setImageBitmap(result)
//                nBitmap = result
//            } ?: Toast.makeText(this, "No image to mask", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun gaussFilter(bitmap: Bitmap): Bitmap {
//        val width = bitmap.width
//        val height = bitmap.height
//        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//
//        val gaussianKernel = arrayOf(
//            arrayOf(1, 2, 1),
//            arrayOf(2, 4, 2),
//            arrayOf(1, 2, 1)
//        )
//        val kernelSum = 16
//
//        for (x in 1 until width - 1) {
//            for (y in 1 until height - 1) {
//                var redSum = 0
//                var greenSum = 0
//                var blueSum = 0
//
//                for (kernelX in -1..1) {
//                    for (kernelY in -1..1) {
//                        val pixel = bitmap.getPixel(x + kernelX, y + kernelY)
//                        val weight = gaussianKernel[kernelX + 1][kernelY + 1]
//
//                        redSum += Color.red(pixel) * weight
//                        greenSum += Color.green(pixel) * weight
//                        blueSum += Color.blue(pixel) * weight
//                    }
//                }
//
//                val red = (redSum / kernelSum).toInt()
//                val green = (greenSum / kernelSum).toInt()
//                val blue = (blueSum / kernelSum).toInt()
//                val newPixel = Color.rgb(red, green, blue)
//
//                blurredBitmap.setPixel(x, y, newPixel)
//            }
//        }
//
//        for (i in 0 until width) {
//            blurredBitmap.setPixel(i, 0, bitmap.getPixel(i, 0))
//            blurredBitmap.setPixel(i, height - 1, bitmap.getPixel(i, height - 1))
//        }
//        for (i in 0 until height) {
//            blurredBitmap.setPixel(0, i, bitmap.getPixel(0, i))
//            blurredBitmap.setPixel(width - 1, i, bitmap.getPixel(width - 1, i))
//        }
//
//        return blurredBitmap
//    }
//
//    private fun scaleImage(bitmap: Bitmap, scaleFactor: Float): Bitmap {
//        val newWidth = (bitmap.width * scaleFactor).toInt()
//        val newHeight = (bitmap.height * scaleFactor).toInt()
//        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
//    }
//
//    private fun saveImageToGallery(bitmap: Bitmap) {
//        val resolver = contentResolver
//        val fileName = System.currentTimeMillis().toString() + ".png"
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//            }
//        }
//        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        try {
//            imageUri?.let { uri ->
//                resolver.openOutputStream(uri)?.use { outputStream ->
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//                }
//                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
//            } ?: Toast.makeText(this, "Failed to create file for image", Toast.LENGTH_LONG).show()
//        } catch (e: IOException) {
//            Toast.makeText(this, "Failed to save image: ${e.localizedMessage}", Toast.LENGTH_LONG)
//                .show()
//            e.printStackTrace()
//        }
//    }
//
//    private fun rotateImage(bitmap: Bitmap?): Bitmap {
//        val width = bitmap!!.width
//        val height = bitmap.height
//        val rotatedBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
//
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                val px = bitmap.getPixel(x, y)
//                rotatedBitmap.setPixel(height - y - 1, x, px)
//            }
//        }
//        return rotatedBitmap
//    }
//    private fun maska(bitmap: Bitmap?): Bitmap {
//        val width = bitmap!!.width
//        val height = bitmap!!.height
//        var newbit = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        newbit = gaussFilter(bitmap!!);
//        var maska = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        for (x in 0 until width) {
//            for (y in 0 until height) {
//                val pixel1 = bitmap!!.getPixel(x, y)
//                val pixel2 = newbit.getPixel(x, y)
//
//                val red1 = Color.red(pixel1)
//                val green1 = Color.green(pixel1)
//                val blue1 = Color.blue(pixel1)
//
//                val red2 = Color.red(pixel2)
//                val green2 = Color.green(pixel2)
//                val blue2 = Color.blue(pixel2)
//
//                val diffRed = Math.abs(red1 - red2) * 2 + red1
//                val diffGreen = Math.abs(green1 - green2) * 2 + green1
//                val diffBlue = Math.abs(blue1 - blue2) * 2 + blue1
//
//                val resultPixel = Color.rgb(diffRed, diffGreen, diffBlue)
//                maska.setPixel(x, y, resultPixel)
//            }
//        }
//
//        return maska;
//    }
//}
/*
package com.example.mobileproject

import android.content.ContentValues
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
import android.widget.Toast
import java.io.IOException


class Activity1 : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var canvasContainer: FrameLayout
    lateinit var retouchCanvas: Canvas
    lateinit var touchView: View
    var nBitmap: Bitmap? = null
    var nBitmapOriginal: Bitmap? = null
    private var isRetouchMode: Boolean = false
    private var mosaicRadius: Int = 20
    private var touchBitmap: Bitmap? = null
    private var path = android.graphics.Path()

    private val paths = mutableListOf<android.graphics.Path>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)

        imageView = findViewById(R.id.imageViewFirstFragment)

        canvasContainer = findViewById(R.id.retouchCanvasContainer)

        val imageUri = intent.getStringExtra("imageUri") ?: return
        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            nBitmap = bitmap // сохранить bitmap в переменную
            nBitmapOriginal = nBitmap
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
            canvasContainer.visibility =
                if (isRetouchMode)
                    View.VISIBLE
                else
                    View.GONE
        }

        val buttonBack: ImageButton = findViewById(R.id.back_button)
        buttonBack.setOnClickListener {
            finish() // для закрытия текущей активности
        }

        val buttonReset: ImageButton = findViewById(R.id.reset_button)
        buttonReset.setOnClickListener {
            nBitmap?.let {
                nBitmap = nBitmapOriginal
                imageView.setImageBitmap(nBitmap)
            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
        }

        val buttonSave: ImageButton = findViewById(R.id.save_button)
        buttonSave.setOnClickListener {
            nBitmap?.let {
                saveImageToGallery(it)
            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
        }

        val buttonRotate: ImageButton = findViewById(R.id.rotate_button)
        buttonRotate.setOnClickListener {
            nBitmap?.let { bitmap ->
                val rotatedBitmap = rotateImage(bitmap)
                imageView.setImageBitmap(rotatedBitmap)
                nBitmap = rotatedBitmap
            } ?: Toast.makeText(this, "No image to rotate", Toast.LENGTH_SHORT).show()
        }

        val buttonScale: ImageButton = findViewById(R.id.crop_button)
        buttonScale.setOnClickListener {
            nBitmap?.let { bitmap ->
                val scaleFactor = 10.0f
                val scaledBitmap = trilinearInterpolation(bitmap, scaleFactor)
                imageView.setImageBitmap(scaledBitmap)
                nBitmap = scaledBitmap
            } ?: Toast.makeText(this, "No image to crop", Toast.LENGTH_SHORT).show()
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

        val buttonRotate1: ImageButton = findViewById(R.id.rotate_button)
        buttonRotate1.setOnClickListener {
            val rotateFragment = RotateFragment()
            val bundle = Bundle().apply {
                putParcelable("imageBitmap", nBitmap)
            }
            rotateFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, rotateFragment)
                .commit()
        }

        val buttonMaska: ImageButton = findViewById(R.id.unsharp_button)
        buttonMaska.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = maska(bitmap);
                imageView.setImageBitmap(result)
                nBitmap = result
            } ?: Toast.makeText(this, "No image to mask", Toast.LENGTH_SHORT).show()
        }
    }

    private fun gaussFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val gaussianKernel = arrayOf(
            arrayOf(1, 2, 1),
            arrayOf(2, 4, 2),
            arrayOf(1, 2, 1)
        )
        val kernelSum = 16

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var redSum = 0
                var greenSum = 0
                var blueSum = 0

                for (kernelX in -1..1) {
                    for (kernelY in -1..1) {
                        val pixel = bitmap.getPixel(x + kernelX, y + kernelY)
                        val weight = gaussianKernel[kernelX + 1][kernelY + 1]

                        redSum += Color.red(pixel) * weight
                        greenSum += Color.green(pixel) * weight
                        blueSum += Color.blue(pixel) * weight
                    }
                }

                val red = (redSum / kernelSum).toInt()
                val green = (greenSum / kernelSum).toInt()
                val blue = (blueSum / kernelSum).toInt()
                val newPixel = Color.rgb(red, green, blue)

                blurredBitmap.setPixel(x, y, newPixel)
            }
        }

        for (i in 0 until width) {
            blurredBitmap.setPixel(i, 0, bitmap.getPixel(i, 0))
            blurredBitmap.setPixel(i, height - 1, bitmap.getPixel(i, height - 1))
        }
        for (i in 0 until height) {
            blurredBitmap.setPixel(0, i, bitmap.getPixel(0, i))
            blurredBitmap.setPixel(width - 1, i, bitmap.getPixel(width - 1, i))
        }

        return blurredBitmap
    }

    private fun scaleImage(bitmap: Bitmap, scaleFactor: Float): Bitmap {
        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun trilinearInterpolation(input: Bitmap, scaleFactor: Float): Bitmap {
        //val first = Bitmap.createBitmap(input.width+1, input.height+1, input.config)
        //val second = Bitmap.createBitmap(input.width-1, input.height-1, input.config)
        val c = 1 - ((1 - scaleFactor) / 2)
        val secondMip = bilinearInterpolation(input, c)
        val firstresalt = bilinearInterpolation(input, scaleFactor)
        val secondresalt = bilinearInterpolation(secondMip, scaleFactor / c)
        var newBitmap = Bitmap.createBitmap(
            Math.round(input.width * scaleFactor),
            Math.round(input.height * scaleFactor), input.config
        )
        var alpha: Int
        var red: Int
        var green: Int
        var blue: Int
        for (j in 0 until newBitmap.height - 1) {
            for (i in 0 until newBitmap.width - 1) {
                if (i < secondresalt.width || j < secondresalt.height) {
                    val pix1 = firstresalt.getPixel(i, j)
                    val pix2 = secondresalt.getPixel(i, j)

                    alpha = (Color.alpha(pix1) + Color.alpha(pix2)) / 2
                    red = (Color.red(pix1) + Color.red(pix2)) / 2
                    green = (Color.green(pix1) + Color.green(pix2)) / 2
                    blue = (Color.blue(pix1) + Color.blue(pix2)) / 2

                    val newPixel: Int = Color.argb(alpha, red, green, blue)
                    newBitmap.setPixel(i, j, newPixel)
                } else {
                    newBitmap.setPixel(i, j, firstresalt.getPixel(i, j))
                }

            }
        }
        return newBitmap
    }

    fun bilinearInterpolation(input: Bitmap, scaleFactor: Float): Bitmap {
        val newWidth = (input.width * scaleFactor).toInt()
        val newHeight = (input.height * scaleFactor).toInt()
        val output = Bitmap.createBitmap(newWidth, newHeight, input.config)


        val widthRatio = input.width.toFloat() / newWidth
        val heightRatio = input.height.toFloat() / newHeight

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                val originX = x * widthRatio
                val originY = y * heightRatio

                val x1 = originX.toInt()
                val y1 = originY.toInt()
                val x2 = if (x1 + 1 < input.width) x1 + 1 else x1
                val y2 = if (y1 + 1 < input.height) y1 + 1 else y1

                val xPercent = originX - x1
                val yPercent = originY - y1

                val topLeft = input.getPixel(x1, y1)
                val topRight = input.getPixel(x2, y1)
                val bottomLeft = input.getPixel(x1, y2)
                val bottomRight = input.getPixel(x2, y2)

                val top = bilinearInterpolateColor(topLeft, topRight, xPercent)
                val bottom = bilinearInterpolateColor(bottomLeft, bottomRight, xPercent)
                val color = bilinearInterpolateColor(top, bottom, yPercent)

                output.setPixel(x, y, color)
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

    private fun rotateImage(bitmap: Bitmap?): Bitmap {
        val width = bitmap!!.width
        val height = bitmap.height
        val rotatedBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val px = bitmap.getPixel(x, y)
                rotatedBitmap.setPixel(height - y - 1, x, px)
            }
        }
        return rotatedBitmap
    }

    private fun maska(bitmap: Bitmap?): Bitmap {
        val width = bitmap!!.width
        val height = bitmap!!.height
        var newbit = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newbit = gaussFilter(bitmap!!);
        var maska = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel1 = bitmap!!.getPixel(x, y)
                val pixel2 = newbit.getPixel(x, y)

                val red1 = Color.red(pixel1)
                val green1 = Color.green(pixel1)
                val blue1 = Color.blue(pixel1)

                val red2 = Color.red(pixel2)
                val green2 = Color.green(pixel2)
                val blue2 = Color.blue(pixel2)

                val diffRed = Math.abs(red1 - red2) * 2 + red1
                val diffGreen = Math.abs(green1 - green2) * 2 + green1
                val diffBlue = Math.abs(blue1 - blue2) * 2 + blue1

                val resultPixel = Color.rgb(diffRed, diffGreen, diffBlue)
                maska.setPixel(x, y, resultPixel)
            }
        }

        return maska;
    }
}
*/

package com.example.mobileproject

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.io.IOException

class Activity1 : AppCompatActivity() {
    lateinit var imageView: ImageView
    var nBitmap: Bitmap? = null
    private var imageBitmap2: Bitmap? = null
//    private lateinit var framelayout: FrameLayout;
//    private var imageBitmap1: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)
        imageView = findViewById(R.id.imageViewFirstFragment)

        val imageUri = intent.getStringExtra("imageUri")
        imageView.setImageURI(Uri.parse(imageUri))
        nBitmap = BitmapFactory.decodeFile(imageUri)

        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            nBitmap = bitmap // сохранить bitmap в переменную
            imageBitmap2 = bitmap
        } finally {
            inputStream?.close()
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

        val buttonRotate = findViewById(R.id.rotate_button) as ImageButton
        buttonRotate.setOnClickListener {
            val firstFragment = RotateFragment()
            setNewFragment(firstFragment);
        }

        val buttonScale: ImageButton = findViewById(R.id.crop_button)
        buttonScale.setOnClickListener {
            nBitmap?.let { bitmap ->
                val scaleFactor = 0.5f
                val scaledBitmap = trilinearInterpolation(bitmap, scaleFactor)
                imageView.setImageBitmap(scaledBitmap)
                nBitmap = scaledBitmap
            } ?: Toast.makeText(this, "No image to scale", Toast.LENGTH_SHORT).show()
        }

        val buttonMaska: ImageButton = findViewById(R.id.unsharp_button)
        buttonMaska.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = maska(bitmap);
                imageView.setImageBitmap(result)
                nBitmap = result
            } ?: Toast.makeText(this, "No image to mask", Toast.LENGTH_SHORT).show()
        }
    }

    fun onSeekBarValueChange(value: Int) {
        nBitmap = rotateImage(
            imageBitmap2!!,
            value,
            imageBitmap2!!.width.toDouble(),
            imageBitmap2!!.height.toDouble()
        );
        imageView.setImageBitmap(nBitmap)
    }

    private fun scaleImage1(bitmap: Bitmap, scaleFactor: Double): Bitmap {
        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun setNewFragment(fragment: Fragment) {

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.framelayout, fragment)
            .commit();
    }

    private fun gaussFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val gaussianKernel = arrayOf(
            arrayOf(1, 2, 1),
            arrayOf(2, 4, 2),
            arrayOf(1, 2, 1)
        )
        val kernelSum = 16

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var redSum = 0
                var greenSum = 0
                var blueSum = 0

                for (kernelX in -1..1) {
                    for (kernelY in -1..1) {
                        val pixel = bitmap.getPixel(x + kernelX, y + kernelY)
                        val weight = gaussianKernel[kernelX + 1][kernelY + 1]

                        redSum += Color.red(pixel) * weight
                        greenSum += Color.green(pixel) * weight
                        blueSum += Color.blue(pixel) * weight
                    }
                }

                val red = (redSum / kernelSum).toInt()
                val green = (greenSum / kernelSum).toInt()
                val blue = (blueSum / kernelSum).toInt()
                val newPixel = Color.rgb(red, green, blue)

                blurredBitmap.setPixel(x, y, newPixel)
            }
        }

        for (i in 0 until width) {
            blurredBitmap.setPixel(i, 0, bitmap.getPixel(i, 0))
            blurredBitmap.setPixel(i, height - 1, bitmap.getPixel(i, height - 1))
        }
        for (i in 0 until height) {
            blurredBitmap.setPixel(0, i, bitmap.getPixel(0, i))
            blurredBitmap.setPixel(width - 1, i, bitmap.getPixel(width - 1, i))
        }

        return blurredBitmap
    }

    private fun maska(bitmap: Bitmap?): Bitmap {
        val width = bitmap!!.width
        val height = bitmap!!.height
        var newbit = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newbit = gaussFilter(bitmap!!);
        var maska = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel1 = bitmap!!.getPixel(x, y)
                val pixel2 = newbit.getPixel(x, y)

                val red1 = Color.red(pixel1)
                val green1 = Color.green(pixel1)
                val blue1 = Color.blue(pixel1)

                val red2 = Color.red(pixel2)
                val green2 = Color.green(pixel2)
                val blue2 = Color.blue(pixel2)

                val diffRed = Math.abs(red1 - red2) * 2 + red1
                val diffGreen = Math.abs(green1 - green2) * 2 + green1
                val diffBlue = Math.abs(blue1 - blue2) * 2 + blue1

                val resultPixel = Color.rgb(diffRed, diffGreen, diffBlue)
                maska.setPixel(x, y, resultPixel)
            }
        }

        return maska;
    }


    /*fun scaleImage(inputBitmap: Bitmap, scaleFactor: Double): Bitmap? {

        val inputWidth = inputBitmap.width
        val inputHeight = inputBitmap.height
        val outputWidth = (inputWidth * scaleFactor).toInt()
        val outputHeight = (inputHeight * scaleFactor).toInt()
        val outputBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        val scaleX = inputWidth.toFloat() / outputWidth.toFloat()
        val scaleY = inputHeight.toFloat() / outputHeight.toFloat()
        for (x in 0 until outputWidth) {
            for (y in 0 until outputHeight) {
                val srcX = (x * scaleX).toInt()
                val srcY = (y * scaleY).toInt()
                outputBitmap.setPixel(x, y, inputBitmap.getPixel(srcX, srcY))
            }
        }
        return outputBitmap
    }*/
    private fun scaleImage(inputBitmap: Bitmap, scaleFactor: Double): Bitmap {
        val inputWidth = inputBitmap.width
        val inputHeight = inputBitmap.height
        val outputWidth = (inputWidth * scaleFactor).toInt()
        val outputHeight = (inputHeight * scaleFactor).toInt()
        val outputBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        val scaleX = inputWidth.toFloat() / outputWidth.toFloat()
        val scaleY = inputHeight.toFloat() / outputHeight.toFloat()

        for (x in 0 until outputWidth) {
            for (y in 0 until outputHeight) {
                val srcX = (x * scaleX).toInt()
                val srcY = (y * scaleY).toInt()
                outputBitmap.setPixel(x, y, inputBitmap.getPixel(srcX, srcY))
            }
        }
        return outputBitmap
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

        val changeInput = bilinearInterpolation(input, scaleFactor2)
        val secondResult = bilinearInterpolation(changeInput, scaleFactor / scaleFactor2)
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


//    private fun rotateImage(bitmap: Bitmap?): Bitmap {
//        var matrix = bitmapToMatrix(bitmap!!);
////        var newBitmap =
////            Bitmap.createBitmap(bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888);
//        var newBitmap =
//            Bitmap.createBitmap(bitmap!!.height, bitmap!!.width, Bitmap.Config.ARGB_8888);
//        newBitmap = matrixToBitmap(
//            rotateImage(
//                matrix,
//                90,
//                bitmap!!.height.toDouble(),
//                bitmap!!.width.toDouble()
//            )
//        );
//
//        return newBitmap;
//    }
}