package com.example.mobileproject

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
class MainActivity : AppCompatActivity() {
    val REQUEST_GALLERY = 100
    val REQUEST_CAMERA = 101
    private var nBitmap: Bitmap? = null
    private var isReadPermissionGallery = false
    private var isReadPermissionCamera = false

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            permissions->isReadPermissionGallery=permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]?:isReadPermissionGallery
            isReadPermissionCamera=permissions[android.Manifest.permission.CAMERA]?:isReadPermissionCamera
        }
        requestPermission()

        val ButtonGallery = findViewById(R.id.gallery) as ImageButton
        ButtonGallery.setOnClickListener {

            if(isReadPermissionGallery) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //intent.type = "image/*"
                startActivityForResult(intent, REQUEST_GALLERY)
            }
            else
            {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Нет доступа")
                builder.setMessage("Разрешите доступ к галереи")
                builder.setPositiveButton("ОК") {
                        dialog, id ->  dialog.cancel()
                }
                val dialog = builder.create()
                dialog.show()

            }
        }
        val ButtonCamera = findViewById(R.id.camera)as ImageButton
        ButtonCamera.setOnClickListener{
            if(isReadPermissionCamera) {
                val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                startActivityForResult(cameraIntent,REQUEST_CAMERA)

            }
            else
            {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Нет доступа")
                builder.setMessage("Разрешите доступ к камере")
                builder.setPositiveButton("ОК") {
                        dialog, id ->  dialog.cancel()
                }
                val dialog = builder.create()
                dialog.show()

            }

        }
        /*val ButtonTurn = findViewById(R.id.rotate) as Button
        ButtonTurn.setOnClickListener {
            val bitmap = nBitmap
            val newBitmap = rotateImage(bitmap);
            nBitmap = newBitmap;
            val imageView = findViewById(R.id.imageView1) as ImageView
            imageView.setImageBitmap(newBitmap)

        }*/
    }
    private fun requestPermission()
    {
        isReadPermissionGallery=ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED
        isReadPermissionCamera=ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED
        val permissionRequest:MutableList<String> = ArrayList()
        if(!isReadPermissionGallery)
        {
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(!isReadPermissionCamera)
        {
            permissionRequest.add(android.Manifest.permission.CAMERA)
        }
        if(permissionRequest.isNotEmpty())
        {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY) {
            val photo = data!!.extras!!["data"] as Bitmap
            val clickImageid = findViewById(R.id.imageView1) as ImageView
            clickImageid.setImageBitmap(photo)

        }
    }*/



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if(requestCode==REQUEST_GALLERY) {
                val imageUri = data?.data
                //val fragment1=MainActivity3()

                nBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val clickImageid = findViewById(R.id.imageView1) as ImageView
                clickImageid.setImageBitmap(nBitmap)
                //fragment1.setImage1(nBitmap!!)
                //val myImageView = findViewById(R.id.imageView1) as ImageView
                //myImageView.setImageURI(imageUri)
            }
            else if(requestCode==REQUEST_CAMERA) {
                val photo = data!!.extras!!["data"] as Bitmap
                //val fragment1=MainActivity3()
                //fragment1.setImage1(photo)
                val clickImageid = findViewById(R.id.imageView1) as ImageView
                clickImageid.setImageBitmap(photo)
            }
            /*when(requestCode)
            {
                REQUEST_GALLERY->{
                    data?.data?.let { selectedImage ->
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("imageUri", selectedImage.toString())
                        startActivity(intent)
                    }
                }
                REQUEST_CAMERA->{
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("imageBitmap", imageBitmap)
                    startActivity(intent)
                }*/

            //val imageUri = data?.data
            //val fragment1=FirstFragment()
            //fragment1.setImage(imageUri!!)
            //nBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            //show_URI_as_Bitmap(imageUri)
        }
    }

    /*fun show_URI_as_Bitmap(uri: Uri?) {
        val myImageView = findViewById(R.id.imageView1) as ImageView
        myImageView.setImageURI(uri)
    }*/
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

