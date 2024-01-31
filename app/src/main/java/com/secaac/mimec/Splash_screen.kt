package com.secaac.mimec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class Splash_screen : AppCompatActivity() {
    private lateinit var imgLogo: ImageView
    private lateinit var txtTitulo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        imgLogo = findViewById<ImageView>(R.id.imgLogo)
        txtTitulo = findViewById<TextView>(R.id.txtTitulo)

        imgLogo.alpha = 0f
        imgLogo.animate().setDuration(2500).alpha(1f).withEndAction {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        txtTitulo.alpha = 0f
        txtTitulo.animate().setDuration(2500).alpha(1f).withEndAction {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

    }

}