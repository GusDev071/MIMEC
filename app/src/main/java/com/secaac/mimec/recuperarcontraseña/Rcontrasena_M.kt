package com.secaac.mimec

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Rcontrasena_M : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rcontrasena_m)

        auth = FirebaseAuth.getInstance()

        val cambiarButton: Button = findViewById(R.id.Cambiar)
        val volverButton: Button = findViewById(R.id.volver)

        cambiarButton.setOnClickListener {
            cambiarContrasena()
        }

        volverButton.setOnClickListener {
            // Volver a la actividad IniciM
            onBackPressed()
        }
    }

    private fun cambiarContrasena() {
        val actualPassword = findViewById<EditText>(R.id.CActual).text.toString()
        val nuevaPassword = findViewById<EditText>(R.id.CNueva).text.toString()
        val confirmarPassword = findViewById<EditText>(R.id.CConfir).text.toString()

        // Verificar que la nueva contraseña y la confirmación coincidan
        if (nuevaPassword == confirmarPassword) {
            // Cambiar la contraseña en Firebase
            val user = auth.currentUser
            user?.updatePassword(nuevaPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Contraseña cambiada con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al cambiar la contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }
    }
}
