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
package com.example.mobileproject

import android.content.ContentValues
import android.content.Context
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
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class Activity1 : AppCompatActivity() {
    lateinit var imageView: ImageView
    var nBitmap: Bitmap? = null
    var nBitmapOriginal: Bitmap? = null
//    private lateinit var retouchImageView: RetouchImageView
    private var isRetouchMode: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)
        imageView = findViewById(R.id.imageViewFirstFragment)
//        retouchImageView = findViewById(R.id.imageViewFirstFragment)
        val imageUri = intent.getStringExtra("imageUri") ?: return
        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            nBitmap = bitmap // сохранить bitmap в переменную
            nBitmapOriginal = nBitmap
        } finally {
            inputStream?.close()
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
                val scaleFactor = 0.8f
                val scaledBitmap = scaleImage(bitmap, scaleFactor)
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
//
//        retouchImageView = findViewById(R.id.retouchImageView)  // Убедитесь, что вы настроили правильный ID в layout
//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.your_image)
//        retouchImageView.setBitmap(bitmap)
//
//        retouchImageView.setBrushSize(30)
//        retouchImageView.setRetouchFactor(10)

        // Инициализация кнопки ретуширования
//        val buttonRetouch: ImageButton = findViewById(R.id.retouch_button)
//        buttonRetouch.setOnClickListener {
//            isRetouchMode = !isRetouchMode
//            retouchImageView.isRetouchEnabled = isRetouchMode
//            val message = if (isRetouchMode) "Retouch mode enabled" else "Retouch mode disabled"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        }

        val buttonRetouch: ImageButton = findViewById(R.id.retouch_button)
//        buttonRetouch.setOnClickListener {
//            val retouchFragment = RetouchFragment()
//            val bundle = Bundle().apply {
//                putParcelable("imageBitmap", nBitmap)
//            }
//            retouchFragment.arguments = bundle
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.framelayout, retouchFragment)
//                .commit()
//        }

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
