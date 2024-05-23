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
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import org.opencv.android.OpenCVLoader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    val REQUEST_GALLERY = 100
    val REQUEST_CAMERA = 101
    private var nBitmap: Bitmap? = null
    private var isReadPermissionGallery = false
    private var isReadPermissionCamera = false

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var cameraPhotoPath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { permissions ->
                isReadPermissionGallery =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: isReadPermissionGallery
                isReadPermissionCamera =
                    permissions[android.Manifest.permission.CAMERA] ?: isReadPermissionCamera
            }
        requestPermission()

        val ButtonGallery = findViewById(R.id.gallery) as ImageButton
        ButtonGallery.setOnClickListener {

            if (isReadPermissionGallery) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(intent, REQUEST_GALLERY)
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Нет доступа")
                builder.setMessage("Разрешите доступ к галереи")
                builder.setPositiveButton("ОК") { dialog, id ->
                    dialog.cancel()
                }
                val dialog = builder.create()
                dialog.show()

            }
        }
        val ButtonCamera = findViewById(R.id.camera) as ImageButton
        ButtonCamera.setOnClickListener {
            if (isReadPermissionCamera) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                startActivityForResult(cameraIntent, REQUEST_CAMERA)

            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Нет доступа")
                builder.setMessage("Разрешите доступ к камере")
                builder.setPositiveButton("ОК") { dialog, id ->
                    dialog.cancel()
                }
                val dialog = builder.create()
                dialog.show()
            }

        }

        val buttonSplain: ImageButton = findViewById(R.id.spline)
        buttonSplain.setOnClickListener {
            startActivity(Intent(this, Cavas::class.java))
            finish()
        }
    }

    private fun requestPermission() {
        isReadPermissionGallery = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED
        isReadPermissionCamera =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        val permissionRequest: MutableList<String> = ArrayList()
        if (!isReadPermissionGallery) {
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isReadPermissionCamera) {
            permissionRequest.add(android.Manifest.permission.CAMERA)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        var bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_GALLERY) {
//                val imageUri = data?.data
//                //передача изображения в новый активити
//                val intent = Intent(this, Activity1::class.java)
//                intent.putExtra("imageUri", imageUri.toString())
//
//                startActivity(intent)
//
//                nBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//
//            } else if (requestCode == REQUEST_CAMERA) {
//                nBitmap = data!!.extras!!["data"] as Bitmap
//                //передача изображения в новый активити
//                val imageUri = bitmapToUri(this, nBitmap!!)
//                val intent = Intent(this, Activity1::class.java)
//
//                intent.putExtra("imageUri", imageUri.toString())
//
//                startActivity(intent)
//            }
//
//        }
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                val imageUri = data?.data
                //передача изображения в новый активити
                val intent = Intent(this, Activity1::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
                nBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            } else if (requestCode == REQUEST_CAMERA) {
                nBitmap = data!!.extras!!["data"] as Bitmap
                //передача изображения в новый активити
                val imageUri = bitmapToUri(this, nBitmap!!)
                val intent = Intent(this, Activity1::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)

            }

        }
    }
}


/*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
    insets
}*/