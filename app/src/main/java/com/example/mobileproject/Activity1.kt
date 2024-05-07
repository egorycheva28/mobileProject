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
import android.widget.Toast
import java.io.IOException


class Activity1 : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private var nBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)
        imageView = findViewById(R.id.imageViewFirstFragment)
        val imageUri = intent.getStringExtra("imageUri") ?: return
        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            nBitmap = bitmap // сохранить bitmap в переменную
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

        val buttonRotate: ImageButton = findViewById(R.id.rotate_button)
        buttonRotate.setOnClickListener {
            nBitmap?.let { bitmap ->
                val rotatedBitmap = rotateImage(bitmap)
                imageView.setImageBitmap(rotatedBitmap)
                nBitmap = rotatedBitmap
            } ?: Toast.makeText(this, "No image to rotate", Toast.LENGTH_SHORT).show()
        }

        val buttonBlackAndWhite: ImageButton = findViewById(R.id.bw_button)
        buttonBlackAndWhite.setOnClickListener {
            nBitmap?.let { bitmap ->
                val bwBitmap = convertToBlackAndWhite(bitmap)
                imageView.setImageBitmap(bwBitmap)
                nBitmap = bwBitmap
            } ?: Toast.makeText(this, "No image to use filter on", Toast.LENGTH_SHORT).show()
        }

        val buttonRed: ImageButton = findViewById(R.id.red_button)
        buttonRed.setOnClickListener {
            nBitmap?.let { bitmap ->
                val bwBitmap = convertToRed(bitmap)
                imageView.setImageBitmap(bwBitmap)
                nBitmap = bwBitmap
            } ?: Toast.makeText(this, "No image to use filter on", Toast.LENGTH_SHORT).show()
        }

        val buttonBrightness: ImageButton = findViewById(R.id.brightness_button)
        buttonBrightness.setOnClickListener {
            nBitmap?.let { bitmap ->
                val bwBitmap = makeBrighter(bitmap)
                imageView.setImageBitmap(bwBitmap)
                nBitmap = bwBitmap
            } ?: Toast.makeText(this, "No image to use filter on", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToBlackAndWhite(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val grayScaling = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

                val newPixel = Color.rgb(gray, gray, gray)

                grayScaling.setPixel(x, y, newPixel)
            }
        }

        return grayScaling
    }

    private fun convertToRed(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val redBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val newPixel = Color.rgb(red, 0, 0)

                redBitmap.setPixel(x, y, newPixel)
            }
        }

        return redBitmap
    }

    private fun makeBrighter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val brightBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)

                var red = Color.red(pixel) + 40
                var green = Color.green(pixel) + 40
                var blue = Color.blue(pixel) + 40

                red = if (red > 255) 255 else red
                green = if (green > 255) 255 else green
                blue = if (blue > 255) 255 else blue

                val newPixel = Color.rgb(red, green, blue)

                brightBitmap.setPixel(x, y, newPixel)
            }
        }

        return brightBitmap
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
}
