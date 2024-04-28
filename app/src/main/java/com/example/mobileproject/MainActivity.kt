package com.example.mobileproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.provider.MediaStore
class MainActivity : AppCompatActivity() {
    val REQUEST_GALLERY=100
    private var nBitmap:Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        var myButton = findViewById(R.id.fragment1) as Button
        myButton.setOnClickListener {
            intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GALLERY)
        }
        var myButton1 = findViewById(R.id.button2) as Button
        myButton1.setOnClickListener{
            var bitmap=nBitmap
            var newBitmap=rotateImage(bitmap);
            nBitmap=newBitmap;
            var imageView = findViewById(R.id.imageView1) as ImageView
            imageView.setImageBitmap(newBitmap)

        }
    }

    private fun rotateImage(bitmap: Bitmap?): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(90.0f)

        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY) {

            var imageUri = data?.data
            nBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            show_URI_as_Bitmap(imageUri)

        }
    }
    fun show_URI_as_Bitmap(uri: Uri?)
    {
        var myImageView = findViewById(R.id.imageView1) as ImageView
        //val inputStream = contentResolver.openInputStream(uri)
        //val bitmap = BitmapFactory.decodeStream(inputStream)
        myImageView.setImageURI(uri)
    }

    /*fun rotateImage(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90.0f)

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }*/
    //val newBitmap=rotateImage(bitmap);
    //val imageView = findViewById(R.id.imageView1) as ImageView
    //imageView.setImageBitmap(newBitmap)
}
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        /*val myButton = findViewById(R.id.fragment1) as Button
        val frameLayout = findViewById(R.id.frame_layout) as FrameLayout
        fun setNewFragment()
        {
            val fragment=FirstFragment()
            getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout,fragment)
                .replace(R.id.frame_layout,fragment)

        }
        myButton.setOnClickListener {
            val fragment = FirstFragment()
            supportFragmentManager.beginTransaction()
                //.add(R.id.frame_layout,fragment)
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,REQUEST_GALLERY)
        }*/

         */

