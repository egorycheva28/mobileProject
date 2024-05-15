package com.example.mobileproject

import android.graphics.Bitmap
import android.graphics.Color

data class PixelRGB(val red: Int, val green: Int, val blue: Int)

//var matrix= bitmapToMatrix(imageBitmap1!!);
//var n=rotateImage(matrix,50,89.0,8.9)
//var newBitmap=Bitmap.createBitmap(imageBitmap1!!.width, imageBitmap1!!.height, Bitmap.Config.ARGB_8888);
//newBitmap= matrixToBitmap(rotateImage(matrix,50,89.0,8.9));
fun bitmapToMatrix(bitmap: Bitmap): Array<Array<PixelRGB>> {
    val pixels = Array(bitmap.height) { Array(bitmap.width) { PixelRGB(0, 0, 0) } }

    for (i in 0 until bitmap.height) {
        for (j in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(j, i)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            pixels[i][j] = PixelRGB(red, green, blue)
        }
    }

    return pixels
}

fun matrixToBitmap(matrix: Array<Array<PixelRGB>>): Bitmap {
    val height = matrix.size
    val width = matrix[0].size
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (i in 0 until height) {
        for (j in 0 until width) {
            val pixel = matrix[i][j]
            val color = Color.argb(255, pixel.red, pixel.green, pixel.blue)
            bitmap.setPixel(j, i, color)
        }
    }

    return bitmap
}

fun rotateImage(
    matrix1: Array<Array<PixelRGB>>,
    angle: Int,
    width: Double,
    height: Double
): Bitmap {
    //val width = pixel.size
    //val height = pixel.size

    val centerX = width / 2
    val centerY = height / 2
    //var angle=0;

    var maxRow = 0.0;
    var maxCol = 0.0;
    var minRow = 0.0;
    var minCol = 0.0;
    if (angle >= 0 && angle <= 90) {
        maxRow =
            centerX + (width - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        maxCol =
            centerY + (height - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (width - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minRow =
            centerX + (0 - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (height - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minCol =
            centerY + (0 - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
    } else if (angle > 90 && angle <= 180) {
        maxRow =
            centerX + (0 - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        maxCol =
            centerY + (0 - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (width - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minRow =
            centerX + (width - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (height - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minCol =
            centerY + (height - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
    } else if (angle > 180 && angle <= 270) {
        maxRow =
            centerX + (0 - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (height - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        maxCol =
            centerY + (0 - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minRow =
            centerX + (width - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minCol =
            centerY + (height - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (width - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
    } else if (angle > 270 && angle <= 360) {
        maxRow =
            centerX + (width - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (height - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        maxCol =
            centerY + (height - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minRow =
            centerX + (0 - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (0 - centerY) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
        minCol =
            centerY + (height - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (width - centerX) * Math.sin(
                Math.toRadians(angle.toDouble())
            );
    }
    val newWidth = maxRow - minRow + 1
    val newHeight = maxCol - minCol + 1

    var newX: Double;
    var newY: Double;
    if (newWidth < width) {
        newX = width
    } else {
        newX = newWidth
    }
    if (newHeight < height) {
        newY = height
    } else {
        newY = newHeight
    }

    var matrixNew = Array(newX.toInt()) { Array(newY.toInt()) { PixelRGB(0, 0, 0) } };
    var bit = Bitmap.createBitmap(newX.toInt(), newY.toInt(), Bitmap.Config.ARGB_8888);
    for (i in 0 until newX.toInt()) {
        for (j in 0 until newY.toInt()) {
            if (i < width && j < height) {
                var x =
                    centerX + (i - centerX) * Math.cos(Math.toRadians(angle.toDouble())) - (j - centerY) * Math.sin(
                        Math.toRadians(angle.toDouble())
                    )
                var y =
                    centerY + (j - centerY) * Math.cos(Math.toRadians(angle.toDouble())) - (i - centerX) * Math.sin(
                        Math.toRadians(angle.toDouble())
                    )
                var newI = x - minRow
                var newJ = y - minCol
                var intNew = newI.toInt()
                var intNew2 = newJ.toInt()
                if (intNew < newX.toInt() && intNew < newY.toInt()) {
                    matrixNew[intNew][intNew2] = matrix1[i][j]



                    if (angle >= 0 && angle <= 180) {
                        if (newJ > 0 && matrixNew[intNew][intNew2 - 1].blue == -1 && matrixNew[intNew][intNew2 - 1].green == -1 && matrixNew[intNew][intNew2 - 1].red == -1) {
                            if (i > 0 && j > 0) {
                                while (matrixNew[intNew][intNew2 - 1].blue == -1 && matrixNew[intNew][intNew2 - 1].green == -1 && matrixNew[intNew][intNew2 - 1].red == -1) {
                                    newJ--;
                                    if (matrixNew[intNew + 1][intNew2 + 1].blue != -1 && matrixNew[intNew + 1][intNew2 + 1].green != -1 && matrixNew[intNew + 1][intNew2 + 1].red != -1) {
                                        break;
                                    }
                                }
                                matrixNew[intNew][intNew2] = matrix1[i][j]
                            }
                        }

                    } else {
                        if (newJ + 1 < newY && matrixNew[intNew][intNew2 + 1].blue == -1 && matrixNew[intNew][intNew2 + 1].green == -1 && matrixNew[intNew][intNew2 + 1].red == -1) {

                            if (i > 0 && j > 0) {
                                while (matrixNew[intNew][intNew2 + 1].blue == -1 && matrixNew[intNew][intNew2 + 1].green == -1 && matrixNew[intNew][intNew2 + 1].red == -1) {
                                    newJ++;
                                    if (matrixNew[intNew - 1][intNew2 - 1].blue != -1 && matrixNew[intNew - 1][intNew2 - 1].green != -1 && matrixNew[intNew - 1][intNew2 - 1].red != -1) {
                                        break;
                                    }
                                }
                                matrixNew[intNew][intNew2] = matrix1[i][j];
                            }

                        }
                    }
                }
            }
        }
    }
    bit = matrixToBitmap(matrixNew);
    return bit;
}