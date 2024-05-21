package com.example.mobileproject

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import java.io.InputStream
import java.io.OutputStream

class FaceFragment : Fragment() {
    private var nBitmap: Bitmap? = null
    private var faces: List<Rect>? = null

    val cascadePath = "haarcascade_frontalface_alt2.xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nBitmap = it.getParcelable("imageBitmap")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_face, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nBitmap?.let { bitmap ->
            faces = listOfFaces(bitmap, this)
        }

        val buttonFaceDetection: ImageButton = view.findViewById(R.id.face_rectangle_button)
        buttonFaceDetection.setOnClickListener {
            nBitmap?.let { bitmap ->
                val matRectangle = Mat()
                Utils.bitmapToMat(bitmap, matRectangle)
                val faceBitmap = neuralNetworkFace(matRectangle, faces)
                val processedBitmap = Bitmap.createBitmap(
                    faceBitmap.cols(),
                    faceBitmap.rows(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(faceBitmap, processedBitmap)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(processedBitmap)
                    it.nBitmap = processedBitmap
                }
            } ?: Toast.makeText(activity, "Нет изображения для обработки", Toast.LENGTH_SHORT)
                .show()
        }

        val buttonBrightness: ImageButton = view.findViewById(R.id.face_brightness_button)
        buttonBrightness.setOnClickListener {
            nBitmap?.let { bitmap ->
                val brighterBitmap = faceBrightness(bitmap, faces)
                (activity as? Activity1)?.let {
                    Log.d("FilterFragment", "Updating image in Activity1")
                    it.imageView.setImageBitmap(brighterBitmap)
                    it.nBitmap = brighterBitmap
                }
            } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT).show()
        }

        val buttonInversion: ImageButton = view.findViewById(R.id.face_inversion_button)
        buttonInversion.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = faceInvertColors(bitmap, faces);
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "No image to filter", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonMosaic: ImageButton = view.findViewById(R.id.face_mosaic_button)
        buttonMosaic.setOnClickListener {
            nBitmap?.let { bitmap ->
                val mosaicBitmap = faceMosaic(bitmap, faces, 10)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(mosaicBitmap)
                    it.nBitmap = mosaicBitmap
                } ?: Toast.makeText(activity, "No image to blur", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonRed: ImageButton = view.findViewById(R.id.face_red_button)
        buttonRed.setOnClickListener {
            nBitmap?.let { bitmap ->
                val redBitmap = faceRed(bitmap, faces)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(redBitmap)
                    it.nBitmap = redBitmap
                } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val buttonSaturation: ImageButton = view.findViewById(R.id.face_saturation_button)
        buttonSaturation.setOnClickListener {
            nBitmap?.let { bitmap ->
                val saturationBitmap = faceSaturation(bitmap, faces, 1.5f)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(saturationBitmap)
                    it.nBitmap = saturationBitmap
                } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun listOfFaces(input: Bitmap, context: FaceFragment): List<Rect> {
        val cascadeWeights = File(context.requireContext().getExternalFilesDir(null), cascadePath)

        if (!cascadeWeights.exists()) {
            val inputStream: InputStream =
                context.resources.openRawResource(R.raw.haarcascade_frontalface_alt2)
            val outputStream: OutputStream = FileOutputStream(cascadeWeights)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            outputStream.close()
        }

        val faceCascade = CascadeClassifier(cascadeWeights.absolutePath)
        if (faceCascade.empty()) {
            throw RuntimeException("Error loading cascade: ${cascadeWeights.absolutePath}")
        }

        val inputMat = Mat()
        Utils.bitmapToMat(input, inputMat)

        val faces = MatOfRect()
        faceCascade.detectMultiScale(inputMat, faces)

        return faces.toList()
    }

    private fun neuralNetworkFace(input: Mat, faces: List<Rect>?): Mat {
        if (faces != null) {
            for (rect in faces) {
                Imgproc.rectangle(input, rect.tl(), rect.br(), Scalar(154.0, 254.0, 4.0), 2)
            }
        }
        return input
    }

    private fun faceSaturation(input: Bitmap, faces: List<Rect>?, saturationFactor: Float): Bitmap {
        val result = input.copy(input.config, true)

        if (faces != null) {
            for (face in faces) {
                for (y in face.y until face.y + face.height) {
                    for (x in face.x until face.x + face.width) {
                        val pixel = input.getPixel(x, y)
                        val alpha = Color.alpha(pixel)

                        val red = Color.red(pixel)
                        val green = Color.green(pixel)
                        val blue = Color.blue(pixel)

                        val r = red / 255.0f
                        val g = green / 255.0f
                        val b = blue / 255.0f

                        val max = maxOf(r, g, b)
                        val min = minOf(r, g, b)
                        val delta = max - min

                        val lightness = (max + min) / 2.0f
                        val saturation = if (max == min) {
                            0.0f
                        } else {
                            if (lightness > 0.5f) delta / (2.0f - max - min)
                            else delta / (max + min)
                        }

                        var newSaturation = saturation * saturationFactor
                        newSaturation = newSaturation.coerceIn(0.0f, 1.0f)

                        val c = (1.0f - Math.abs(2.0f * lightness - 1.0f)) * newSaturation
                        val m = lightness - c / 2.0f

                        var rNew = (r - lightness) * c / saturation + lightness
                        var gNew = (g - lightness) * c / saturation + lightness
                        var bNew = (b - lightness) * c / saturation + lightness

                        rNew = ((rNew + m) * 255).toInt().coerceIn(0, 255).toFloat()
                        gNew = ((gNew + m) * 255).toInt().coerceIn(0, 255).toFloat()
                        bNew = ((bNew + m) * 255).toInt().coerceIn(0, 255).toFloat()

                        val newPixel = Color.argb(alpha, rNew.toInt(), gNew.toInt(), bNew.toInt())
                        result.setPixel(x, y, newPixel)

                    }
                }
            }
        }

        return result
    }

    private fun faceBrightness(input: Bitmap, faces: List<Rect>?): Bitmap {
        val result = input.copy(input.config, true)

        if (faces != null) {
            for (face in faces) {
                for (y in face.y until face.y + face.height) {
                    for (x in face.x until face.x + face.width) {
                        val pixel = input.getPixel(x, y)

                        var red = Color.red(pixel) + 40
                        var green = Color.green(pixel) + 40
                        var blue = Color.blue(pixel) + 40

                        red = if (red > 255) 255 else red
                        green = if (green > 255) 255 else green
                        blue = if (blue > 255) 255 else blue

                        val newPixel = Color.rgb(red, green, blue)

                        result.setPixel(x, y, newPixel)
                    }
                }
            }
        }

        return result
    }

    private fun faceInvertColors(input: Bitmap, faces: List<Rect>?): Bitmap {
        val result = input.copy(input.config, true)

        if (faces != null) {
            for (face in faces) {
                for (y in face.y until face.y + face.height) {
                    for (x in face.x until face.x + face.width) {
                        val pixel = input.getPixel(x, y)

                        val red = Color.red(pixel)
                        val green = Color.green(pixel)
                        val blue = Color.blue(pixel)

                        val invertedRed = 255 - red
                        val invertedGreen = 255 - green
                        val invertedBlue = 255 - blue

                        val newPixel = Color.rgb(invertedRed, invertedGreen, invertedBlue)

                        result.setPixel(x, y, newPixel)
                    }
                }
            }
        }

        return result
    }

    private fun faceMosaic(input: Bitmap, faces: List<Rect>?, blockSize: Int): Bitmap {
        val result = input.copy(input.config, true)

        if (faces != null) {
            for (face in faces) {
                for (x in face.x until face.x + face.width step blockSize) {
                    for (y in face.y until face.y + face.height step blockSize) {
                        mosaicBlock(input, result, x, y, blockSize, face)
                    }
                }
            }
        }

        return result
    }

    private fun mosaicBlock(
        originalBitmap: Bitmap,
        mosaicBitmap: Bitmap,
        startX: Int,
        startY: Int,
        blockSize: Int,
        face: Rect
    ) {
        val width = originalBitmap.width
        val height = originalBitmap.height

        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var pixelCount = 0

        for (x in startX until (startX + blockSize).coerceAtMost(face.x + face.width)) {
            for (y in startY until (startY + blockSize).coerceAtMost(face.y + face.height)) {
                // Ensure we're still within the bounds of the face
                if (x < face.x + face.width && x < width && y < face.y + face.height && y < height) {
                    val pixel = originalBitmap.getPixel(x, y)
                    redSum += Color.red(pixel)
                    greenSum += Color.green(pixel)
                    blueSum += Color.blue(pixel)
                    pixelCount++
                }
            }
        }

        if (pixelCount > 0) {
            val red = (redSum / pixelCount).toInt()
            val green = (greenSum / pixelCount).toInt()
            val blue = (blueSum / pixelCount).toInt()
            val averageColor = Color.rgb(red, green, blue)

            for (x in startX until (startX + blockSize).coerceAtMost(face.x + face.width)) {
                for (y in startY until (startY + blockSize).coerceAtMost(face.y + face.height)) {
                    // Ensure we're still within the bounds of the face
                    if (x < face.x + face.width && x < width && y < face.y + face.height && y < height) {
                        mosaicBitmap.setPixel(x, y, averageColor)
                    }
                }
            }
        }
    }

    private fun faceRed(input: Bitmap, faces: List<Rect>?): Bitmap {
        val width = input.width
        val height = input.height
        val redBitmap = input.copy(input.config, true)

        if (faces != null) {
            for (face in faces) {
                for (x in face.x until face.x + face.width) {
                    for (y in face.y until face.y + face.height) {
                        if (x in 0 until width && y in 0 until height) {
                            val pixel = input.getPixel(x, y)

                            val red = Color.red(pixel)
                            val newPixel = Color.rgb(red, 0, 0)

                            redBitmap.setPixel(x, y, newPixel)
                        }
                    }
                }
            }
        }

        return redBitmap
    }
}
