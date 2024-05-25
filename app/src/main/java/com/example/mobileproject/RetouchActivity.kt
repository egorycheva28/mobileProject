//import com.example.mobileproject.R

//package com.example.mobileproject
//
//import android.content.ContentValues
//import android.content.Intent
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Bundle
//import android.widget.ImageButton
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import android.graphics.BitmapFactory
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.view.MotionEvent
//import android.view.View
//import android.widget.FrameLayout
//import android.widget.HorizontalScrollView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentTransaction
//import java.io.IOException
//
//class RetouchActivity : AppCompatActivity(),
//    RotateFragment.OnSeekBarChangeListener1, MaskingFragment.OnSeekBarChangeListener3,
//    ScaleFragment.OnSeekBarChangeListener2 {
//    lateinit var imageView: ImageView
//    var nBitmap: Bitmap? = null
//    private var imageBitmap2: Bitmap? = null
//    private var imageBitmap3: Bitmap? = null
//    private var imageBitmap4: Bitmap? = null
//    private var isScale = false
//    private var isReset = false
//    lateinit var canvasContainer: FrameLayout
//    lateinit var retouchCanvas: Canvas
//    lateinit var touchView: View
//    private var isRetouchMode: Boolean = false
//    private var mosaicRadius: Int = 10
//    private var touchBitmap: Bitmap? = null
//    private var path = android.graphics.Path()
//
//    private val paths = mutableListOf<android.graphics.Path>()
//
////    private lateinit var framelayout: FrameLayout;
////    private var imageBitmap1: Bitmap? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity1)
//        imageView = findViewById(R.id.imageViewFirstFragment)
//
//        canvasContainer = findViewById(R.id.retouchCanvasContainer)
//
//        val imageUri = intent.getStringExtra("imageUri")
//        imageView.setImageURI(Uri.parse(imageUri))
//        nBitmap = BitmapFactory.decodeFile(imageUri)
//
//
//        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
//        try {
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            imageView.setImageBitmap(bitmap)
//            nBitmap = bitmap // сохранить bitmap в переменную
//            imageBitmap2 = bitmap
//            imageBitmap3 = bitmap
//            imageBitmap4 = bitmap
//            touchBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//            retouchCanvas = Canvas(touchBitmap!!)
//        } finally {
//            inputStream?.close()
//        }
//        touchView = object : View(this) {
//            override fun onDraw(canvas: Canvas) {
//                super.onDraw(canvas)
//                canvas.drawBitmap(touchBitmap!!, 0f, 0f, null)
//
//                for (path in paths) {
//                    drawMosaic(path, canvas)
//                }
//            }
//
//            override fun onTouchEvent(e: MotionEvent): Boolean {
//                val touchX = e.x
//                val touchY = e.y
//
//                when (e.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        path.moveTo(touchX, touchY)
//                        return true
//                    }
//
//                    MotionEvent.ACTION_MOVE -> {
//                        path.lineTo(touchX, touchY)
//                        applyMosaic(touchX.toInt(), touchY.toInt())
//                    }
//
//                    MotionEvent.ACTION_UP -> {
//                        paths.add(android.graphics.Path(path))
//                        path.reset()
//                    }
//
//                    else -> return false
//                }
//                invalidate()
//                return true
//            }
//
//            private fun applyMosaic(x: Int, y: Int) {
//                val width = touchBitmap!!.width
//                val height = touchBitmap!!.height
//                val startX = (x - mosaicRadius).coerceAtLeast(0)
//                val startY = (y - mosaicRadius).coerceAtLeast(0)
//                val endX = (x + mosaicRadius).coerceAtMost(width - 1)
//                val endY = (y + mosaicRadius).coerceAtMost(height - 1)
//
//                var redSum = 0
//                var greenSum = 0
//                var blueSum = 0
//                var pixelCount = 0
//
//                for (i in startX..endX) {
//                    for (j in startY..endY) {
//                        val pixel = touchBitmap!!.getPixel(i, j)
//                        redSum += Color.red(pixel)
//                        greenSum += Color.green(pixel)
//                        blueSum += Color.blue(pixel)
//                        pixelCount++
//                    }
//                }
//                if (pixelCount > 0) {
//                    val avgRed = (redSum / pixelCount).coerceIn(0, 255)
//                    val avgGreen = (greenSum / pixelCount).coerceIn(0, 255)
//                    val avgBlue = (blueSum / pixelCount).coerceIn(0, 255)
//                    val averageColor = Color.rgb(avgRed, avgGreen, avgBlue)
//
//                    val paintMosaic = Paint().apply {
//                        color = averageColor
//                        style = Paint.Style.FILL
//                    }
//                    for (i in startX..endX) {
//                        for (j in startY..endY) {
//                            retouchCanvas.drawPoint(i.toFloat(), j.toFloat(), paintMosaic)
//                        }
//                    }
//                }
//            }
//
//            private fun drawMosaic(path: android.graphics.Path, canvas: Canvas) {
//                val paint = Paint()
//                paint.style = Paint.Style.STROKE
//                paint.color = Color.TRANSPARENT
//                paint.strokeWidth = 12f
//                canvas.drawPath(path, paint)
//            }
//        }
//
//        canvasContainer.addView(touchView)
//
//        val buttonRetouch: ImageButton = findViewById(R.id.retouch_button)
//        buttonRetouch.setOnClickListener {
//            isRetouchMode = !isRetouchMode
//            canvasContainer.visibility =
//                if (isRetouchMode)
//                    View.VISIBLE
//                else
//                    View.GONE
//        }
//
//        val btnGoBack = findViewById<ImageButton>(R.id.btnGoBack1)
//        btnGoBack.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        val buttonSave: ImageButton = findViewById(R.id.save_button1)
//        buttonSave.setOnClickListener {
//            nBitmap?.let {
//                saveImageToGallery(it)
//            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
//        }
//
//        val buttonBefore: ImageButton = findViewById(R.id.reset_button)
//        buttonBefore.setOnClickListener {
//            isScale = false
//            nBitmap?.let {
//                nBitmap = imageBitmap3
//                imageView.setImageBitmap(nBitmap)
//            } ?: Toast.makeText(this, "Image not available", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setNewFragment(fragment: Fragment) {
//        getSupportFragmentManager().beginTransaction()
//            .replace(R.id.framelayout, fragment)
//            .commit();
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
//}
package com.example.mobileproject

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class RetouchActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var canvasContainer: FrameLayout
    private lateinit var retouchCanvas: Canvas
    private lateinit var touchBitmap: Bitmap
    private var mosaicRadius: Int = 10
    private var path = Path()
    private val paths = mutableListOf<Path>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_retouch)

        imageView = findViewById(R.id.retouchImageView)
        canvasContainer = findViewById(R.id.retouchCanvasContainer)

        val byteArray = intent.getByteArrayExtra("imageBitmap")
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        imageView.setImageBitmap(bitmap)
        touchBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        retouchCanvas = Canvas(touchBitmap)

        val btnGoBack1 = findViewById<ImageButton>(R.id.btnGoBack1)
        btnGoBack1.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            startActivity(intent)
            finish()
        }

        val touchView = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                canvas.drawBitmap(touchBitmap, 0f, 0f, null)

                for (path in paths) {
                    drawMosaic(path, canvas)
                }
            }

            override fun onTouchEvent(event: MotionEvent): Boolean {
                val touchX = event.x
                val touchY = event.y

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        path.moveTo(touchX, touchY)
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        path.lineTo(touchX, touchY)
                        applyMosaic(touchX.toInt(), touchY.toInt())
                    }

                    MotionEvent.ACTION_UP -> {
                        paths.add(Path(path))
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
    }
}
