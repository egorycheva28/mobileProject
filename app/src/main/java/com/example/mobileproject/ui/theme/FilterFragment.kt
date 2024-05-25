package com.example.mobileproject // https://habr.com/ru/articles/735316/

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
import kotlin.math.exp
import kotlin.math.pow

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
                val result = grayscale(bitmap)
                (activity as? Activity1)?.let {
                    Log.d("FilterFragment", "Updating image in Activity1")
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                }
            } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
        }

        val buttonBrightness: ImageButton = view.findViewById(R.id.brightness_button)
        buttonBrightness.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = makeBrighter(bitmap)
                (activity as? Activity1)?.let {
                    Log.d("FilterFragment", "Updating image in Activity1")
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                }
            } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
        }

        val buttonInversion: ImageButton = view.findViewById(R.id.inversion_button)
        buttonInversion.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = invertion(bitmap);
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonSepia: ImageButton = view.findViewById(R.id.sepia_button)
        buttonSepia.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = sepia(bitmap);
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonContrast: ImageButton = view.findViewById(R.id.contrast_button)
        buttonContrast.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = contrast(bitmap, 2.0f);
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonMosaic: ImageButton = view.findViewById(R.id.mosaic_button)
        buttonMosaic.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = mosaicFilter(bitmap, 7)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonBlur: ImageButton = view.findViewById(R.id.blur_button)
        buttonBlur.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = gaussFilter(bitmap, 3)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }

        }

        val buttonRed: ImageButton = view.findViewById(R.id.red_button)
        buttonRed.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = red(bitmap)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }
        }
        val buttonSaturation: ImageButton = view.findViewById(R.id.saturation_button)
        buttonSaturation.setOnClickListener {
            nBitmap?.let { bitmap ->
                val result = saturation(bitmap, 1.5f)
                (activity as? Activity1)?.let {
                    it.imageView.setImageBitmap(result)
                    it.nBitmap = result
                } ?: Toast.makeText(activity, "@strings/no_image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun grayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        var grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var newPixels = mutableListOf<Int>()

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

        grayscaleBitmap.setPixels(newPixels.toIntArray(), 0, width, 0, 0, width, height)

        return grayscaleBitmap
    }

    private fun saturation(bitmap: Bitmap, saturationFactor: Float): Bitmap {
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

                var newRed = (r - lightness) * c / saturation + lightness
                var newGreen = (g - lightness) * c / saturation + lightness
                var newBlue = (b - lightness) * c / saturation + lightness

                newRed = ((newRed + m) * 255).toInt().coerceIn(0, 255).toFloat()
                newGreen = ((newGreen + m) * 255).toInt().coerceIn(0, 255).toFloat()
                newBlue = ((newBlue + m) * 255).toInt().coerceIn(0, 255).toFloat()

                val newPixel = Color.argb(alpha, newRed.toInt(), newGreen.toInt(), newBlue.toInt())
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

                red =
                    if (red > 255)
                        255
                    else
                        red
                green =
                    if (green > 255)
                        255
                    else green
                blue =
                    if (blue > 255)
                        255
                    else blue

                val newPixel = Color.rgb(red, green, blue)

                brightBitmap.setPixel(x, y, newPixel)
            }
        }

        return brightBitmap
    }

    private fun invertion(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        var invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var newPixels = mutableListOf<Int>()

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

    private fun sepia(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        var sepiaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var newPixels = mutableListOf<Int>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val newRed =
                    ((red * 0.393) + (green * 0.769) + (blue * 0.189)).coerceAtMost(255.0).toInt()
                val newGreen =
                    ((red * 0.349) + (green * 0.686) + (blue * 0.168)).coerceAtMost(255.0).toInt()
                val newBlue =
                    ((red * 0.272) + (green * 0.534) + (blue * 0.131)).coerceAtMost(255.0).toInt()

                val newPixel = Color.rgb(newRed, newGreen, newBlue)

                newPixels.add(newPixel)
            }
        }

        sepiaBitmap.setPixels(newPixels.toIntArray(), 0, width, 0, 0, width, height)

        return sepiaBitmap
    }

    private fun contrast(bitmap: Bitmap, coeff: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val contrastedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val contrast = (coeff - 1) * 2

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val alpha = Color.alpha(pixel)

                val newRed = ((red / 255.0 - 0.5) * contrast + 0.5).coerceIn(0.0, 1.0) * 255.0
                val newGreen = ((green / 255.0 - 0.5) * contrast + 0.5).coerceIn(0.0, 1.0) * 255.0
                val newBlue = ((blue / 255.0 - 0.5) * contrast + 0.5).coerceIn(0.0, 1.0) * 255.0

                val newPixel = Color.argb(alpha, newRed.toInt(), newGreen.toInt(), newBlue.toInt())
                contrastedBitmap.setPixel(x, y, newPixel)
            }
        }

        return contrastedBitmap
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

    /*private fun gaussFilter(bitmap: Bitmap): Bitmap {
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
    }*/

    private fun red(bitmap: Bitmap): Bitmap {
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

    private fun clamp(value: Int, min: Int = 0, max: Int = 255): Int {
        return Math.min(max, Math.max(min, value))
    }

    fun gaussFilter(bitmap: Bitmap, radius: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //val pixels = IntArray(bitmap.width * bitmap.height)
        //bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        //val newPixels = IntArray(pixels.size)
        /*val gaussianKernel = arrayOf(
            arrayOf(2, 2, 2),
            arrayOf(2, 4, 2),
            arrayOf(2, 2, 2)
        )
        val kernelSum = 20*/

        //val kernel = createGaussianKernel(radius)
        for (x in 0 until width) {
            for (y in 0 until height) {
                var redSum = 0
                var greenSum = 0
                var blueSum = 0
                var kernelSum = 0.0

                for (kernelX in -radius..radius) {
                    for (kernelY in -radius..radius) {
                        //val weight = kernel[kernelX + radius][kernelY + radius]
                        val weight = gaussianKernel(kernelX, kernelY, radius / 3.0)
                        kernelSum += weight
                        if ((x + kernelX) in 0 until width && (y + kernelY) in 0 until height) {
                            val pixel = bitmap.getPixel(x + kernelX, y + kernelY)

                            //val pixel = pixels[(y + kernelY) * bitmap.width + (x + kernelX)]
                            redSum += Color.red(pixel) * weight.toInt()
                            greenSum += Color.green(pixel) * weight.toInt()
                            blueSum += Color.blue(pixel) * weight.toInt()

                        }
                    }
                }

                //val red = (redSum / kernelSum).toInt()
                //val green = (greenSum / kernelSum).toInt()
                //val blue = (blueSum / kernelSum).toInt()
                //newPixels[y * bitmap.width + x] = Color.rgb(redSum, greenSum, blueSum)
                val newPixel = Color.rgb(clamp(redSum), clamp(greenSum), clamp(blueSum))

                blurredBitmap.setPixel(x, y, newPixel)
            }
        }
        //val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        //resultBitmap.setPixels(newPixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        /*for (i in 0 until width) {
            blurredBitmap.setPixel(i, 0, bitmap.getPixel(i, 0))
            blurredBitmap.setPixel(i, height - 1, bitmap.getPixel(i, height - 1))
        }
        for (i in 0 until height) {
            blurredBitmap.setPixel(0, i, bitmap.getPixel(0, i))
            blurredBitmap.setPixel(width - 1, i, bitmap.getPixel(width - 1, i))
        }*/
        //return resultBitmap

        return blurredBitmap
    }

    private fun gaussianKernel(x: Int, y: Int, sigma: Double): Double {
        val exp1 = -(x.toDouble().pow(2) + y.toDouble().pow(2)) / (2 * sigma.pow(2));
        return exp(exp1) / (2 * Math.PI * sigma.pow(2));
    }
}
