package com.secaac.mimec

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecuperarContra : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.editTextTextEmailAddress)
        val enviarButton: Button = findViewById(R.id.enviar)
        val volverInicioButton: Button = findViewById(R.id.volverI)

        enviarButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                enviarCorreoRecuperacion(email)
            } else {
                mostrarAlerta("Error", "Por favor, ingrese un correo electrónico válido.")
            }
        }

        volverInicioButton.setOnClickListener {
            // Volver a la actividad principal (MainActivity)
            regresarAInicio()
        }
    }

    private fun enviarCorreoRecuperacion(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mostrarAlerta(
                        "Éxito",
                        "Se envió un enlace de recuperación a su correo. Pulse volver a inicio."
                    )
                } else {
                    mostrarAlerta(
                        "Error",
                        "Correo incorrecto o no registrado. Por favor, verifique la información."
                    )
                }
            }
    }

    private fun regresarAInicio() {
        // Regresar a la actividad principal (MainActivity)
        val regre = Intent(this, MainActivity::class.java)
        startActivity(regre)
        finish()
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("OK") { _, _ -> }

        val dialog = builder.create()
        dialog.show()
    }
}
