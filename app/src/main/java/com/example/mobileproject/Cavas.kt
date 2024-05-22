package com.example.mobileproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity



class Cavas : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cavas)

        val myView = findViewById<MyView>(R.id.my_view)
        val drawButton: Button = findViewById(R.id.draw_button)
        val clearButton = findViewById<Button>(R.id.clear_button)

        drawButton.setOnClickListener {
            myView.drawSpline()
        }
        clearButton.setOnClickListener {
            myView.clearCanvas()
        }

        val btnGoBack = findViewById<Button>(R.id.btnGoBack)
        btnGoBack.setOnClickListener {
            finish()
        }

    }
}

