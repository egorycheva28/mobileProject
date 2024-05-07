package com.example.mobileproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory


class Activity1 : AppCompatActivity() {
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity1)
        //вывод изображения
        imageView = findViewById(R.id.imageViewFirstFragment)
        val imageUri = intent.getStringExtra("imageUri") ?: return
        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        val bitmap = BitmapFactory.decodeStream(inputStream)
        imageView.setImageBitmap(bitmap)


        val buttonBack = findViewById(R.id.back_button) as ImageButton
        buttonBack.setOnClickListener {
            val activity = Intent(this, MainActivity::class.java)
            startActivity(activity)
        }
    }
}
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_first, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val imageView = view.findViewById<ImageView>(R.id.imageViewFirstFragment)
//        arguments?.getParcelable<Bitmap>("image")?.let {
//            imageView.setImageBitmap(it)
//        }
//    }

