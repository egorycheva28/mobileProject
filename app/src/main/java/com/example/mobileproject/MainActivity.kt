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
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val REQUEST_GALLERY = 100
    val REQUEST_CAMERA = 101
    private var isReadPermissionGallery = false
    private var isReadPermissionCamera = false

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val ButtonGallery: ImageButton = findViewById(R.id.gallery)
        ButtonGallery.setOnClickListener {
            if (isReadPermissionGallery) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, REQUEST_GALLERY)
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

        val ButtonCamera: ImageButton = findViewById(R.id.camera)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                val imageUri = data?.data
                val galleryIntent = Intent(this, MainActivity3::class.java)
                galleryIntent.putExtra("imageUri", imageUri.toString())
                startActivity(galleryIntent)
            } else if (requestCode == REQUEST_CAMERA) {
                val imageBitmap = data!!.extras!!["data"] as Bitmap
                val imageUri = bitmapToUri(this, imageBitmap)
                val cameraIntent = Intent(this, MainActivity3::class.java)
                cameraIntent.putExtra("imageUri", imageUri.toString())
                startActivity(cameraIntent)
            }
        }
    }

    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}

    





