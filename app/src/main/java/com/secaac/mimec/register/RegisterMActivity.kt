package com.secaac.mimec.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secaac.mimec.LoginMActivity
import com.secaac.mimec.R

class RegisterMActivity : AppCompatActivity() {
    private lateinit var txtIni2: TextView
    private lateinit var etNombreCompleto: EditText
    private lateinit var etNombreTaller: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var etDireccion: EditText
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
        etDireccion = findViewById(R.id.txtMeca2)
        btnRegistrar = findViewById(R.id.button)

        txtIni2.setOnClickListener { Ini2() }
        btnRegistrar.setOnClickListener { registrarUsuario() }
    }

    private fun Ini2() {
        val ini2 = Intent(this, LoginMActivity::class.java)
        startActivity(ini2)
        finish() // Cierra la actividad actual después de iniciar sesión
    }

    private fun registrarUsuario() {
    val nombreCompleto = etNombreCompleto.text.toString().trim()
    val nombreTaller = etNombreTaller.text.toString().trim()
    val correo = etCorreo.text.toString().trim()
    val contraseña = etContraseña.text.toString().trim()
    val direccion = etDireccion.text.toString().trim()

    if (nombreCompleto.isEmpty() || nombreTaller.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
        Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        return
    }

    FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Crear un objeto usuario
                val user = hashMapOf(
                    "nombreCompleto" to nombreCompleto,
                    "nombreTaller" to nombreTaller,
                    "correo" to correo,
                    "direccion" to direccion,
                    "contraseña" to contraseña,
                    "usuario" to "mechanic" // Asegúrate de que este campo sea "mechanic" para los mecánicos
                )

                // Obtener el ID del usuario
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                // Guardar el objeto usuario en Firestore
                if (userId != null) {
                    db.collection("usuarios").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario almacenado con ID: $userId", Toast.LENGTH_SHORT).show()

                            // Redirigir al usuario a la actividad de inicio de sesión
                            val intent = Intent(this, LoginMActivity::class.java)
                            startActivity(intent)
                            finish() // Cierra la actividad actual para que no pueda volver atrás
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al almacenar usuario", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // Si el registro falla, muestra un mensaje al usuario.
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }
}
}