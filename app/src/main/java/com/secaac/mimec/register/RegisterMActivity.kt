package com.secaac.mimec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterMActivity : AppCompatActivity() {
    private lateinit var txtIni2: TextView
    private lateinit var etNombreCompleto: EditText
    private lateinit var etNombreTaller: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var btnRegistrar: Button
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_mactivity)

        txtIni2 = findViewById(R.id.txtIni2)
        etNombreCompleto = findViewById(R.id.editText)
        etNombreTaller = findViewById(R.id.txtMeca)
        etCorreo = findViewById(R.id.editTextTextEmailAddress)
        etContraseña = findViewById(R.id.editTextPassword)
        btnRegistrar = findViewById(R.id.button)

        txtIni2.setOnClickListener { Ini2() }
        btnRegistrar.setOnClickListener { registrarUsuario() }
    }

    private fun Ini2() {
        val ini2 = Intent(this, LoginMActivity::class.java)
        startActivity(ini2)
    }

    private fun registrarUsuario() {
        val nombreCompleto = etNombreCompleto.text.toString().trim()
        val nombreTaller = etNombreTaller.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        if (nombreCompleto.isEmpty() || nombreTaller.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si el correo ya existe en la base de datos
        db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "El correo ya está en uso, intenta con otro", Toast.LENGTH_SHORT).show()
                } else {
                    // Crear un objeto usuario
                    val user = hashMapOf(
                        "nombreCompleto" to nombreCompleto,
                        "nombreTaller" to nombreTaller,
                        "correo" to correo,
                        "contraseña" to contraseña,
                        "usuario" to "mechanic" // Asegúrate de que este campo sea "mechanic" para los mecánicos
                    )

                    // Guardar el objeto usuario en Firestore
                    db.collection("usuarios") // Asegúrate de que estás guardando en la colección correcta
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Usuario almacenado con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                            // Redirigir al usuario a la actividad de inicio de sesión
                            val intent = Intent(this, LoginMActivity::class.java)
                            startActivity(intent)
                            finish() // Cierra la actividad actual para que no pueda volver atrás
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al almacenar usuario", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar el correo", Toast.LENGTH_SHORT).show()
            }
    }
}