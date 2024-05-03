package com.example.mobileproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraButton = findViewById<ImageButton>(R.id.camera_button)
        val galleryButton = findViewById<ImageButton>(R.id.gallery_button)

        // нажатие на кнопку камеры
        cameraButton.setOnClickListener {
            takePicture()
        }

        // нажатие на кнопку галереи
        galleryButton.setOnClickListener {
            importFromGallery()
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            // Обработка ошибки, если приложение камеры недоступно
            e.printStackTrace()
        }
    }

    private fun importFromGallery() {
        val openGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(openGalleryIntent, REQUEST_IMAGE_OPEN)
        } catch (e: Exception) {
            // Обработка ошибки, если приложение галереи недоступно
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_OPEN = 2
    }
}
