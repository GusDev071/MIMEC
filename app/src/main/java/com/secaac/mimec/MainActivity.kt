package com.secaac.mimec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btnU: Button
    private lateinit var btnM: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnU = findViewById<Button>(R.id.btnU)
        btnM = findViewById<Button>(R.id.btnM)

        btnU.setOnClickListener{Login()}
        btnM.setOnClickListener{Login2()}
    }

    private fun Login(){
        val login = Intent(this, LoginUActivity::class.java)
        startActivity(login)
        finish()
    }

    private fun Login2(){
        val log2 = Intent(this, LoginMActivity::class.java)
        startActivity(log2)
        finish()
    }
}