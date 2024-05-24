package com.example.mobileproject

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

fun Masking(bitmap: Bitmap?, coefficient: Double, radius: Int, porog: Double): Bitmap {
    val width = bitmap!!.width
    val height = bitmap.height

    var gaussBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    gaussBitmap = gaussFilter(bitmap, radius);

    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    for (x in 0 until width) {
        for (y in 0 until height) {
            val originalPixel = bitmap.getPixel(x, y)
            val gaussPixel = gaussBitmap.getPixel(x, y)

            val originRed = Color.red(originalPixel)
            val originGreen = Color.green(originalPixel)
            val originBlue = Color.blue(originalPixel)

            val gaussRed = Color.red(gaussPixel)
            val gaussGreen = Color.green(gaussPixel)
            val gaussBlue = Color.blue(gaussPixel)

            var differentRed = 0.0
            var differentGreen = 0.0
            var differentBlue = 0.0
            if (originalPixel > porog) {
                differentRed = (originRed - gaussRed) * coefficient + originRed - porog
                differentGreen = (originGreen - gaussGreen) * coefficient + originGreen - porog
                differentBlue = (originBlue - gaussBlue) * coefficient + originBlue - porog
            } else if (originalPixel < -porog) {
                differentRed = (originRed - gaussRed) * coefficient + originRed + porog
                differentGreen = (originGreen - gaussGreen) * coefficient + originGreen + porog
                differentBlue = (originBlue - gaussBlue) * coefficient + originBlue + porog
            } else {
                differentRed = 0.0
                differentGreen = 0.0
                differentBlue = 0.0

            }
            val resultPixel = Color.rgb(
                compare(differentRed.toInt()),
                compare(differentGreen.toInt()),
                compare(differentBlue.toInt())
            )
            result.setPixel(x, y, resultPixel)
        }
    }

    return result;
}

private fun compare(value: Int, min: Int = 0, max: Int = 255): Int {
    return max.coerceAtMost(min.coerceAtLeast(value))
}

fun gaussFilter(bitmap: Bitmap, radius: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (x in 0 until width) {
        for (y in 0 until height) {
            var redSum = 0
            var greenSum = 0
            var blueSum = 0
            var kernelSum = 0.0

            for (kernelX in -radius..radius) {
                for (kernelY in -radius..radius) {
                    val weight = gaussianKernel(kernelX, kernelY, radius / 3.0)
                    kernelSum += weight
                    if ((x + kernelX) in 0 until width && (y + kernelY) in 0 until height) {
                        val pixel = bitmap.getPixel(x + kernelX, y + kernelY)

                        redSum += Color.red(pixel) * weight.toInt()
                        greenSum += Color.green(pixel) * weight.toInt()
                        blueSum += Color.blue(pixel) * weight.toInt()

                    }
                }
            }

            val red = (redSum / kernelSum).toInt()
            val green = (greenSum / kernelSum).toInt()
            val blue = (blueSum / kernelSum).toInt()
            val newPixel = Color.rgb(compare(red), compare(green), compare(blue))

            blurredBitmap.setPixel(x, y, newPixel)
        }
    }

    return blurredBitmap
}

private fun gaussianKernel(x: Int, y: Int, sigma: Double): Double {
    val exp1 = -(x.toDouble().pow(2) + y.toDouble().pow(2)) / (2 * sigma.pow(2));
    return exp(exp1) / (2 * Math.PI * sigma.pow(2));
}



