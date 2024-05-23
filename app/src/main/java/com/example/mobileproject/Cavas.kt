package com.example.mobileproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity



class Cavas : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spline)

        val myView = findViewById<MyView>(R.id.my_view)
        val drawButton: ImageButton = findViewById(R.id.draw_button)
        val clearButton = findViewById<ImageButton>(R.id.clear_button)

        drawButton.setOnClickListener {
            myView.drawSpline()
        }
        clearButton.setOnClickListener {
            myView.clearCanvas()
        }

        val btnGoBack = findViewById<ImageButton>(R.id.btnGoBack)
        btnGoBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}