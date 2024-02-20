package com.secaac.mimec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Splash_screen : AppCompatActivity() {
    private lateinit var imgLogo: ImageView
    private lateinit var txtTitulo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT

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