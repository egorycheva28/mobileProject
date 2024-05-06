package com.example.mobileproject

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainActivity3 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttons:ArrayList<Filters>
    private lateinit var adapterr:Adapterr
    private lateinit var imageView:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        //вывод изображения
        imageView=findViewById(R.id.imageView3)
        val imageUri = intent.getStringExtra("imageUri")
        imageView.setImageURI(Uri.parse(imageUri))

        val buttonBack=findViewById(R.id.back) as Button
        buttonBack.setOnClickListener{
            val activity= Intent(this, MainActivity::class.java)
            startActivity(activity)
        }
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        //val adapter = Adapter(buttons)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        buttons = ArrayList()
        buttons.add(Filters(R.drawable.download,"mozaika","gugvu"))
        buttons.add(Filters(R.drawable.download,"Цветокоррекция","gugvu"))
        buttons.add(Filters(R.drawable.download,"Масштабирование","gugvu"))
        buttons.add(Filters(R.drawable.download,"Распознавание лиц","gugvu"))
        buttons.add(Filters(R.drawable.download,"Векторный редактор","gugvu"))
        buttons.add(Filters(R.drawable.download,"Ретуширование","gugvu"))
        buttons.add(Filters(R.drawable.download,"Нерезкое маскирование","gugvu"))
        buttons.add(Filters(R.drawable.download,"mozaika","gugvu"))
        adapterr = Adapterr(buttons)
        recyclerView.adapter=adapterr
    }


}
