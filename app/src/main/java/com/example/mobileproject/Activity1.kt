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
    private lateinit var cascadeClassifier: CascadeClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)
        imageView = findViewById(R.id.imageViewFirstFragment)

//        if (!loadCascade()) {
//            Toast.makeText(this, "Ошибка загрузки каскадного файла.", Toast.LENGTH_LONG).show()
//            return
//        }


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

        val buttonScale: ImageButton = findViewById(R.id.crop_button)
        buttonScale.setOnClickListener {
            nBitmap?.let { bitmap ->
                val scaleFactor = 0.8f
                val scaledBitmap = scaleImage(bitmap, scaleFactor)
                imageView.setImageBitmap(scaledBitmap)
                nBitmap = scaledBitmap
            } ?: Toast.makeText(this, "No image to crop", Toast.LENGTH_SHORT).show()
        }

        val buttonFaceDetection: ImageButton = findViewById(R.id.face_button)
        buttonFaceDetection.setOnClickListener {
            nBitmap?.let { bitmap ->
                val mat = Mat()
                Utils.bitmapToMat(bitmap, mat)
                val faceBitmap = faceDetection(mat, this) // Передаем this как context
                val processedBitmap = Bitmap.createBitmap(
                    faceBitmap.cols(),
                    faceBitmap.rows(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(faceBitmap, processedBitmap)
                imageView.setImageBitmap(processedBitmap)
                nBitmap = processedBitmap
            } ?: Toast.makeText(this, "Нет изображения для обработки", Toast.LENGTH_SHORT).show()
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

    fun faceDetection(input: Mat, context: Context): Mat {
        val cascadeFile =
            File(context.getExternalFilesDir(null), "haarcascade_frontalface_alt2.xml")
        if (!cascadeFile.exists()) {
            val inputStream: InputStream =
                context.resources.openRawResource(R.raw.haarcascade_frontalface_alt2)
            val outputStream: OutputStream = FileOutputStream(cascadeFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            outputStream.close()
        }

        val faceCascade = CascadeClassifier(cascadeFile.absolutePath)
        if (faceCascade.empty()) {
            println("Error loading cascade: ${cascadeFile.absolutePath}")
        } else {
            val faces = MatOfRect()
            faceCascade.detectMultiScale(input, faces)
            for (rect: Rect in faces.toArray()) {
                Imgproc.rectangle(input, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 2)
            }
        }

        return input
    }


}
