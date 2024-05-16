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

class FilterFragment : Fragment() {
    private var nBitmap: Bitmap? = null

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
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonBlackAndWhite: ImageButton = view.findViewById(R.id.bw_button)
        buttonBlackAndWhite.setOnClickListener {
            nBitmap?.let { bitmap ->
                val bwBitmap = convertToBlackAndWhite(bitmap)
                (activity as? Activity1)?.let {
                    Log.d("FilterFragment", "Updating image in Activity1")
                    it.imageView.setImageBitmap(bwBitmap)
                    it.nBitmap = bwBitmap
                }
            } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT).show()
        }
        val buttonBrightness: ImageButton = view.findViewById(R.id.brightness_button)
        buttonBrightness.setOnClickListener {
            nBitmap?.let { bitmap ->
                val brighterBitmap = makeBrighter(bitmap)
                (activity as? Activity1)?.let {
                    Log.d("FilterFragment", "Updating image in Activity1")
                    it.imageView.setImageBitmap(brighterBitmap)
                    it.nBitmap = brighterBitmap
                }
            } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT).show()
        }
        val buttonInversion: ImageButton = view.findViewById(R.id.inversion_button)
        buttonInversion.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = invertColors(bitmap);
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "No image to filter", Toast.LENGTH_SHORT).show()
            }
        }
        val buttonMosaic: ImageButton = view.findViewById(R.id.mosaic_button)
        buttonMosaic.setOnClickListener {
            nBitmap?.let { bitmap ->
                val mosaicBitmap = mosaicFilter(bitmap, 7)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(mosaicBitmap)
                    it.nBitmap = mosaicBitmap
                } ?: Toast.makeText(activity, "No image to blur", Toast.LENGTH_SHORT).show()
            }
        }
        val buttonBlur: ImageButton = view.findViewById(R.id.blur_button)
        buttonBlur.setOnClickListener {
            nBitmap?.let { bitmap ->
                val gaussBitmap = gaussFilter(bitmap)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(gaussBitmap)
                    it.nBitmap = gaussBitmap
                } ?: Toast.makeText(activity, "No image to blur", Toast.LENGTH_SHORT).show()
            }

        }

        val buttonRed: ImageButton = view.findViewById(R.id.red_button)
        buttonRed.setOnClickListener {
            nBitmap?.let { bitmap ->
                val redBitmap = convertToRed(bitmap)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(redBitmap)
                    it.nBitmap = redBitmap
                } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val buttonSaturation: ImageButton = view.findViewById(R.id.saturation_button)
        buttonSaturation.setOnClickListener {
            nBitmap?.let { bitmap ->
                val saturationBitmap = adjustSaturation(bitmap, 1.5f)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(saturationBitmap)
                    it.nBitmap = saturationBitmap
                } ?: Toast.makeText(activity, "No image to use filter on", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun convertToBlackAndWhite(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val grayScaling = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val newPixels = mutableListOf<Int>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val gray = ((red + green + blue) / 3)
                val newPixel = Color.rgb(gray, gray, gray)

                newPixels.add(newPixel)
            }
        }

        grayScaling.setPixels(newPixels.toIntArray(), 0, width, 0, 0, width, height)

        return grayScaling
    }

    private fun adjustSaturation(bitmap: Bitmap, saturationFactor: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val saturatedBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val alpha = Color.alpha(pixel)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                // Преобразование в относительные яркости
                val r = red / 255.0f
                val g = green / 255.0f
                val b = blue / 255.0f

                // Находим max и min относительные яркости
                val max = maxOf(r, g, b)
                val min = minOf(r, g, b)
                val delta = max - min

                // Вычисление lightness и hue
                val lightness = (max + min) / 2.0f
                val saturation = if (max == min) {
                    0.0f // Gray color
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
                saturatedBitmap.setPixel(x, y, newPixel)
            }
        }

        return saturatedBitmap
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

    private fun invertColors(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val newPixels = mutableListOf<Int>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val invertedRed = 255 - red
                val invertedGreen = 255 - green
                val invertedBlue = 255 - blue

                val newPixel = Color.rgb(invertedRed, invertedGreen, invertedBlue)

                newPixels.add(newPixel)
            }
        }

        invertedBitmap.setPixels(newPixels.toIntArray(), 0, width, 0, 0, width, height)

        return invertedBitmap
    }

    private fun mosaicFilter(bitmap: Bitmap, blockSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val mosaicBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width step blockSize) {
            for (y in 0 until height step blockSize) {
                applyMosaicBlock(bitmap, mosaicBitmap, x, y, blockSize)
            }
        }

        return mosaicBitmap
    }

    private fun applyMosaicBlock(
        originalBitmap: Bitmap,
        mosaicBitmap: Bitmap,
        startX: Int,
        startY: Int,
        blockSize: Int
    ) {
        val width = originalBitmap.width
        val height = originalBitmap.height

        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var pixelCount = 0

        for (x in startX until (startX + blockSize).coerceAtMost(width)) {
            for (y in startY until (startY + blockSize).coerceAtMost(height)) {
                val pixel = originalBitmap.getPixel(x, y)
                redSum += Color.red(pixel)
                greenSum += Color.green(pixel)
                blueSum += Color.blue(pixel)
                pixelCount++
            }
        }

        if (pixelCount > 0) {
            val red = (redSum / pixelCount).toInt()
            val green = (greenSum / pixelCount).toInt()
            val blue = (blueSum / pixelCount).toInt()
            val averageColor = Color.rgb(red, green, blue)

            for (x in startX until (startX + blockSize).coerceAtMost(width)) {
                for (y in startY until (startY + blockSize).coerceAtMost(height)) {
                    mosaicBitmap.setPixel(x, y, averageColor)
                }
            }
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

}
