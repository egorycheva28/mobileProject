package com.example.mobileproject

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class PixelRGB(val red: Int, val green: Int, val blue: Int)

fun bitmapToMatrix(bitmap: Bitmap): Array<Array<PixelRGB>> {
    val matrix = Array(bitmap.height) { Array(bitmap.width) { PixelRGB(0, 0, 0) } }

    for (i in 0 until bitmap.height) {
        for (j in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(j, i)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            matrix[i][j] = PixelRGB(red, green, blue)
        }
    }
    return matrix
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
    /* matrix: Array<Array<PixelRGB>>*/
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
        newWidth.toInt()
    }
    val newY = if (newHeight < height) {
        height.toInt()
    } else {
        newHeight.toInt()
    }

    //val matrixNew = Array(newX) { Array(newY) { PixelRGB(0, 0, 0) } };
    var bit = Bitmap.createBitmap(newX, newY, Bitmap.Config.ARGB_8888);

    for (i in 0 until newX) {
        for (j in 0 until newY) {
            if (j < height && i < width) {
                val x =
                    (centerX + (i - centerX) * cos(radian) - (j - centerY) * sin(
                        radian
                    )).toInt()
                var y =
                    (centerY + (j - centerY) * cos(radian) + (i - centerX) * sin(
                        radian
                    )).toInt()
                //val x = (x1 - minRow).toInt()
                //var y = (y1 - minCol).toInt()
                //val intNew = newI.toInt()
                //val intNew2 = newJ.toInt()

                //println(minRow) // 241
                //println(minCol) // 240
                //println(width)//180
                //println(height)//240
                //println(newX)//241
                //println(newY)//179
                //println(bit.width)//241
                //println(bit.height) // 240

                if (x in 0..<newX && y in 0..<newY) {

                    val pixel = bitmap.getPixel(i, j)
                    val color = Color.argb(255, pixel.red, pixel.green, pixel.blue)
                    bit.setPixel(x, y, color)

                    if (angle in 0..180 && i > 0 && j > 0) {

                        while (y > 0 && Color.alpha(bit.getPixel(x, y - 1)) == 0) {
                            y--;
                        }
                    } else if (i > 0 && j > 0) {
                        while (y + 1 < newY && Color.alpha(bit.getPixel(x, y + 1)) == 0) {
                            y++;
                        }
                    }
                    bit.setPixel(x, y, bitmap.getPixel(i, j))
                    /* for (ii in 0 until newY) {
                         for (ji in 0 until newX) {
                             if (ii < width && ji < height) {
                                 var newX1 = ji
                                 val newY1 = ii

                                 if(newX1<newX && newY1<newY) {
                                     if (angle >= 0 && angle <= 180) {
                                         while (newX1 > 0 && Color.alpha(
                                                 bit.getPixel(
                                                     newX1 - 1,
                                                     newY1
                                                 )
                                             ) == 0
                                         ) {
                                             newX1--
                                         }
                                     } else {
                                         while (newX1 + 1 < newX && Color.alpha(
                                                 bit.getPixel(
                                                     newX1 + 1,
                                                     newY1
                                                 )
                                             ) == 0
                                         ) {
                                             newX1++
                                         }
                                     }

                                     bit.setPixel(newX1, newY1, bitmap.getPixel(ji, ii))
                                 }
                             }
                         }
                     }*/
                    //println(intNew)
                    //println(intNew2)
                    //println(i)
                    //println(j)
                    //matrixNew[newI][newJ] = matrix[i][j]
                    /*if (angle in 0..180 && y > 0 && y<height && x<width) {
                            val pixel1 = bitmap.getPixel(x, y-1)

                            val pixel3 = bitmap.getPixel(x+1, y+1)
                            if (y > 0 && pixel1.blue == -1 && pixel1.green == -1 && pixel1.red == -1) {
                                if (i > 0 && j > 0) {
                                    while (pixel1.blue == -1 && pixel1.green == -1 && pixel1.red == -1) {
                                        y--;
                                        if (pixel3.blue != -1 && pixel3.green != -1 && pixel3.red != -1) {
                                            break;
                                        }
                                    }
                                    bit.setPixel(x.toInt(), y.toInt(), color)
                                }
                            }

                        } else if(x>0&& y>0 && y<height && x<width){
                            val pixel2 = bitmap.getPixel(x, y+1)
                            val pixel4 = bitmap.getPixel(x-1, y-1)
                            if (y + 1 < newY && pixel2.blue == -1 && pixel2.green == -1 && pixel2.red == -1) {

                                if (i > 0 && j > 0) {
                                    while (pixel2.blue == -1 && pixel2.green == -1 && pixel2.red == -1) {
                                        y++;
                                        if (pixel4.blue != -1 && pixel4.green != -1 && pixel4.red != -1) {
                                            break;
                                        }
                                    }
                                    bit.setPixel(x.toInt(), y.toInt(), color)
                                }

                            }
                        }*/
                    /*if (angle in 0..180) {
                            if (newJ > 0 && matrixNew[newI][newJ - 1].blue == -1 && matrixNew[newI][newJ - 1].green == -1 && matrixNew[newI][newJ - 1].red == -1) {
                                if (i > 0 && j > 0) {
                                    while (matrixNew[newI][newJ - 1].blue == -1 && matrixNew[newI][newJ - 1].green == -1 && matrixNew[newI][newJ - 1].red == -1) {
                                        newJ--;
                                        if (matrixNew[newI + 1][newJ + 1].blue != -1 && matrixNew[newI + 1][newJ + 1].green != -1 && matrixNew[newI + 1][newJ + 1].red != -1) {
                                            break;
                                        }
                                    }
                                    matrixNew[newI][newJ] = matrix[i][j]
                                }
                            }

                        } else {
                            if (newJ + 1 < newY && matrixNew[newI][newJ + 1].blue == -1 && matrixNew[newI][newJ + 1].green == -1 && matrixNew[newI][newJ + 1].red == -1) {

                                if (i > 0 && j > 0) {
                                    while (matrixNew[newI][newJ + 1].blue == -1 && matrixNew[newI][newJ + 1].green == -1 && matrixNew[newI][newJ + 1].red == -1) {
                                        newJ++;
                                        if (matrixNew[newI - 1][newJ - 1].blue != -1 && matrixNew[newI - 1][newJ - 1].green != -1 && matrixNew[newI - 1][newJ - 1].red != -1) {
                                            break;
                                        }
                                    }
                                    matrixNew[newI][newJ] = matrix[i][j];
                                }

                            }
                        }*/
                }

            }
        }
    }
    //bit = matrixToBitmap(matrixNew);
    return bit;
}