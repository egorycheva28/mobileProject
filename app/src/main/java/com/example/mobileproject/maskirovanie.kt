package com.example.mobileproject

import android.graphics.Bitmap
import android.graphics.Color


fun maska(bitmap: Bitmap?): Bitmap
{
    val width=bitmap!!.width
    val height=bitmap!!.height
    var newbit=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    newbit=gaussFilter(bitmap!!);
    var maska=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    for(x in 0 until width)
    {
        for(y in 0 until height)
        {
            val pixel1 = bitmap!!.getPixel(x, y)
            val pixel2 = newbit.getPixel(x, y)

            val red1 = Color.red(pixel1)
            val green1 = Color.green(pixel1)
            val blue1 = Color.blue(pixel1)

            val red2 = Color.red(pixel2)
            val green2 = Color.green(pixel2)
            val blue2 = Color.blue(pixel2)

            val diffRed = Math.abs(red1 - red2)*1+red1
            val diffGreen = Math.abs(green1 - green2)*1+green1
            val diffBlue = Math.abs(blue1 - blue2)*1+blue1

            val resultPixel = Color.rgb(diffRed, diffGreen, diffBlue)
            maska.setPixel(x,y,resultPixel)
        }
    }


    return maska;
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
        /*fun gaus(bitmap:Bitmap){
        val width = bitmap.width
        val height = bitmap.height
        var newimg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for(y in 0 until height)
        {
            for(x in 0 until width)
            {
                var red = 0
                var green = 0
                var blue = 0
                for(i in 0 until)
                {
                    for()
                    {

                    }
                }
            }
        }
    }*/

