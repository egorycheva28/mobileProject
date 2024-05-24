//https://github.com/1i10/rotation-of-the-image-by-a-given-angle#LittleTheory брала информацию с этого сайта
package com.example.mobileproject

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun rotateImage(
    bitmap: Bitmap,
    angle: Int,
    width: Double,
    height: Double
): Bitmap {

    val centerX = width / 2
    val centerY = height / 2
    val radian = Math.toRadians(angle.toDouble());

    var maxRow = 0.0;
    var maxCol = 0.0;
    var minRow = 0.0;
    var minCol = 0.0;

    if (angle in 0..90) {
        maxRow =
            centerX + (width - centerX) * cos(radian) - (0 - centerY) * sin(
                radian
            );
        maxCol =
            centerY + (height - centerY) * cos(radian) - (width - centerX) * sin(
                radian
            );
        minRow =
            centerX + (0 - centerX) * cos(radian) - (height - centerY) * sin(
                radian
            );
        minCol =
            centerY + (0 - centerY) * cos(radian) - (0 - centerX) * sin(
                radian
            );
    } else if (angle in 91..180) {
        maxRow =
            centerX + (0 - centerX) * cos(radian) - (0 - centerY) * sin(
                radian
            );
        maxCol =
            centerY + (0 - centerY) * cos(radian) - (width - centerX) * sin(
                radian
            );
        minRow =
            centerX + (width - centerX) * cos(radian) - (height - centerY) * sin(
                radian
            );
        minCol =
            centerY + (height - centerY) * cos(radian) - (0 - centerX) * sin(
                radian
            );
    } else if (angle in 181..270) {
        maxRow =
            centerX + (0 - centerX) * cos(radian) - (height - centerY) * sin(
                radian
            );
        maxCol =
            centerY + (0 - centerY) * cos(radian) - (0 - centerX) * sin(
                radian
            );
        minRow =
            centerX + (width - centerX) * cos(radian) - (0 - centerY) * sin(
                radian
            );
        minCol =
            centerY + (height - centerY) * cos(radian) - (width - centerX) * sin(
                radian
            );
    } else if (angle in 271..360) {
        maxRow =
            centerX + (width - centerX) * cos(radian) - (height - centerY) * sin(
                radian
            );
        maxCol =
            centerY + (height - centerY) * cos(radian) - (0 - centerX) * sin(
                radian
            );
        minRow =
            centerX + (0 - centerX) * cos(radian) - (0 - centerY) * sin(
                radian
            );
        minCol =
            centerY + (height - centerY) * cos(radian) - (width - centerX) * sin(
                radian
            );
    }

    val newWidth = (maxRow - minRow).toInt()
    val newHeight = (maxCol - minCol).toInt()

    val newX = if (newWidth < width) {
        width.toInt()
    } else {
        newWidth
    }
    val newY = if (newHeight < height) {
        height.toInt()
    } else {
        newHeight
    }

    val rotateBitmap = Bitmap.createBitmap(newX, newY, Bitmap.Config.ARGB_8888);

    for (i in 0 until newX) {
        for (j in 0 until newY) {
            if (i < width && j < height) {
                val x =
                    (centerX + (i - centerX) * cos(radian) - (j - centerY) * sin(
                        radian
                    )).toInt()
                var y =
                    (centerY + (j - centerY) * cos(radian) + (i - centerX) * sin(
                        radian
                    )).toInt()

                if (x in 0..<newX && y in 0..<newY) {

                    val pixel = bitmap.getPixel(i, j)
                    val color = Color.argb(255, pixel.red, pixel.green, pixel.blue)
                    rotateBitmap.setPixel(x, y, color)

                    if (angle in 0..180 && i > 0 && j > 0) {
                        while (y > 0 && Color.alpha(rotateBitmap.getPixel(x, y - 1)) == 0) {
                            y--;
                        }
                    } else if (i > 0 && j > 0) {
                        while (y + 1 < newY && Color.alpha(rotateBitmap.getPixel(x, y + 1)) == 0) {
                            y++;
                        }
                    }
                    rotateBitmap.setPixel(x, y, bitmap.getPixel(i, j))
                }
            }
        }
    }
    return rotateBitmap
}