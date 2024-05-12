package com.example.mobileproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.net.Uri
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity3 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttons: ArrayList<Filters>
    private lateinit var adapter: Adapter
    private lateinit var framelayout: FrameLayout;
    //private lateinit var imageView: ImageView
    private var imageBitmap1: Bitmap? = null
    private lateinit var imageView:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        //вывод изображения
        imageView = findViewById(R.id.imageView3)
        val imageUri = intent.getStringExtra("imageUri")
        imageView.setImageURI(Uri.parse(imageUri))
        imageBitmap1 = BitmapFactory.decodeFile(imageUri)

        val buttonMaska=findViewById(R.id.unsharp_button)as ImageButton
        buttonMaska.setOnClickListener{
            //var result=Bitmap.createBitmap(imageBitmap1!!.width, imageBitmap1!!.height, Bitmap.Config.ARGB_8888);
            val result=maska(imageBitmap1);
            imageView.setImageBitmap(result)
        }

        val buttonSave = findViewById(R.id.save2) as Button
        buttonSave.setOnClickListener {
            saveImageToGallery(imageBitmap1!!)

        }

        val buttonRotate = findViewById(R.id.rotate_button) as ImageButton
        buttonRotate.setOnClickListener {
            val firstFragment=FirstFragment()
            setNewFragment(firstFragment);
            /*var bitmap1 = imageBitmap1
            var newBitmap1 = rotateImage(bitmap1);
            imageBitmap1 = newBitmap1;
            //val uriimage=bitmapToUri(this,newBitmap)
            //val imageView1 = findViewById(R.id.imageView3) as ImageView
            //imageView1.setImageURI(uriimage)
            imageView.setImageBitmap(imageBitmap1)*/
        }
        val buttonFilter =findViewById(R.id.filter_button)as ImageButton
        buttonFilter.setOnClickListener{
            val secondFragment=SecondFragment()
            setNewFragment(secondFragment);
        }
        //framelayout=findViewById(R.id.framelayout);

        val buttonBack = findViewById(R.id.back) as Button
        buttonBack.setOnClickListener{
            val activity = Intent(this, MainActivity::class.java)
            startActivity(activity)
        }
    }
    private fun setNewFragment(fragment: Fragment) {

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.framelayout, fragment)
            .commit();
    }
    private fun maska(bitmap: Bitmap?): Bitmap
    {
        val width=imageBitmap1!!.width
        val height=imageBitmap1!!.height
        var newbit=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newbit=gaussFilter(imageBitmap1!!);
        var maska=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(x in 0 until width)
        {
            for(y in 0 until height)
            {
                val pixel1 = imageBitmap1!!.getPixel(x, y)
                val pixel2 = newbit.getPixel(x, y)

                val red1 = Color.red(pixel1)
                val green1 = Color.green(pixel1)
                val blue1 = Color.blue(pixel1)

                val red2 = Color.red(pixel2)
                val green2 = Color.green(pixel2)
                val blue2 = Color.blue(pixel2)

                val diffRed = Math.abs(red1 - red2)*2+red1
                val diffGreen = Math.abs(green1 - green2)*2+green1
                val diffBlue = Math.abs(blue1 - blue2)*2+blue1

                val resultPixel = Color.rgb(diffRed, diffGreen, diffBlue)
                maska.setPixel(x,y,resultPixel)
            }
        }


        return maska;
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

        for(x in 0 until width){
            for(y in 0 until height){
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
