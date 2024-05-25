package com.example.mobileproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.net.Uri
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.IOException

class MainActivity3 : AppCompatActivity(),
    FirstFragment.OnSeekBarChangeListener1, MaskingFragment.OnSeekBarChangeListener1, ScalingFragment.OnSeekBarChangeListener1 {
    private lateinit var framelayout: FrameLayout;

    private var imageBitmap1: Bitmap? = null
    private var imageBitmap2: Bitmap? = null
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        //вывод изображения
        //imageView = findViewById(R.id.imageView3)
        //val imageUri = intent.getStringExtra("imageUri")
        //imageView.setImageURI(Uri.parse(imageUri))
        //imageBitmap1 = BitmapFactory.decodeFile(imageUri)

        imageView = findViewById(R.id.imageView3)
        val imageUri = intent.getStringExtra("imageUri") ?: return
        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            imageBitmap1 = bitmap // сохранить bitmap в переменную
            imageBitmap2 = bitmap
        } finally {
            inputStream?.close()
        }

        val buttonMasking = findViewById(R.id.unsharp_button1) as ImageButton
        buttonMasking.setOnClickListener {
            val maskingFragment=MaskingFragment()
            setNewFragment(maskingFragment);
            //var result=Bitmap.createBitmap(imageBitmap1!!.width, imageBitmap1!!.height, Bitmap.Config.ARGB_8888);
            /*imageBitmap1 = Masking(
                imageBitmap1, 0.25, 3, 100.0
            )
            imageView.setImageBitmap(imageBitmap1)*/
            //imageBitmap1= maska(imageBitmap1)
            //imageView.setImageBitmap(imageBitmap1)
        }

        val buttonSave = findViewById(R.id.save2) as Button
        buttonSave.setOnClickListener {
            saveImageToGallery(imageBitmap1!!)
        }

        val buttonRotate = findViewById(R.id.rotate_button) as ImageButton
        buttonRotate.setOnClickListener {
            val firstFragment = FirstFragment()
            setNewFragment(firstFragment);
            //val matrix= bitmapToMatrix(imageBitmap1!!);
            //var newBitmap=Bitmap.createBitmap(imageBitmap1!!.height, imageBitmap1!!.width, Bitmap.Config.ARGB_8888);
            //newBitmap= matrixToBitmap(rotateImage(matrix,90,imageBitmap1!!.width.toDouble(),imageBitmap1!!.height.toDouble()));
            //imageView.setImageBitmap(newBitmap)
            //var n=rotateImage(matrix,50,89.0,8.9,8.9)
            //var newBitmap=Bitmap.createBitmap(imageBitmap1!!.width, imageBitmap1!!.height, Bitmap.Config.ARGB_8888);
            //newBitmap= matrixToBitmap(n);
            //var bitmap1 = imageBitmap1
            //var newBitmap1 = rotateImage(bitmap1);
            //imageBitmap1 = newBitmap1;
            //val uriimage=bitmapToUri(this,newBitmap)
            //val imageView1 = findViewById(R.id.imageView3) as ImageView
            //imageView1.setImageURI(uriimage)
            //imageBitmap1=rotateImage(matrix,60,imageBitmap1!!.width.toDouble(),imageBitmap1!!.height.toDouble());
            //imageView.setImageBitmap(imageBitmap1)

            /*imageView.setImageBitmap(
                rotateImage(
                    imageBitmap1!!,
                    90,
                    imageBitmap1!!.width.toDouble(),
                    imageBitmap1!!.height.toDouble()
                )
            )*/
            //imageView.setImageBitmap(gaussFilter(imageBitmap1!!,2))
        }
        val buttonFilter = findViewById(R.id.filter_button) as ImageButton
        buttonFilter.setOnClickListener {
            val secondFragment = SecondFragment()
            setNewFragment(secondFragment);


        }
        val buttonCrop = findViewById(R.id.crop_button) as ImageButton
        buttonCrop.setOnClickListener {
            val scalingFragment=ScalingFragment()//для масштабирования
            setNewFragment(scalingFragment);//для масштабирования
            //imageView.setImageBitmap(gaussFilter(imageBitmap1!!,3))
        }
        //framelayout=findViewById(R.id.framelayout);

        val buttonBack = findViewById(R.id.back) as Button
        buttonBack.setOnClickListener {
            val activity = Intent(this, MainActivity::class.java)
            startActivity(activity)
        }
    }

    override fun onSeekBarValueChange1(value: Int) {
        imageBitmap1 = rotateImage(
            imageBitmap2!!,
            value,
            imageBitmap2!!.width.toDouble(),
            imageBitmap2!!.height.toDouble()
        );
        imageView.setImageBitmap(imageBitmap1)
    }

    override fun onSeekBarValueChange2(progress1: Int, progress2: Int, progress3: Int) {
        imageBitmap1 =
            Masking(imageBitmap2, progress1.toDouble() / 100.0, progress2, progress3)
        imageView.setImageBitmap(imageBitmap1)
    }
    override fun onSeekBarValueChange3(value: Int) {//для масштабирования
        //imageBitmap1 =
           // Scaling(imageBitmap1, value)
    }

    /*override fun onSeekBarValueChange1(value1: Double,value2:Double,value3:Double) {
        imageBitmap1?.let { bitmap ->
                //val result=Masking(imageBitmap1);
                imageBitmap1=Masking(imageBitmap1, value1,value2,value3);
            imageView.setImageBitmap(imageBitmap1);


            } ?: Toast.makeText(this, "No image to crop", Toast.LENGTH_SHORT).show()
    }*/
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

    fun ggg() {
        imageBitmap1?.let { bitmap ->
            val redBitmap = convertToRed(bitmap)
            imageView.setImageBitmap(redBitmap)
            imageBitmap1 = redBitmap
        } ?: Toast.makeText(this, "No image to use filter on", Toast.LENGTH_SHORT).show()
    }

    private fun setNewFragment(fragment: Fragment) {

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.framelayout, fragment)
            .commit();
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
            imageUri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun rotateImage(bitmap: Bitmap?): Bitmap {
        val height = bitmap!!.height
        val width = bitmap.width

        var rotatedBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                rotatedBitmap.setPixel(height - y - 1, x, pixel)
            }
        }
        return rotatedBitmap
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
}
