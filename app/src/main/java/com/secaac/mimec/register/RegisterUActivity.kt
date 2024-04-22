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
import com.secaac.mimec.LoginUActivity
import com.secaac.mimec.R

class RegisterUActivity : AppCompatActivity() {
    private lateinit var txtIni: TextView
    private lateinit var etNombre: EditText
    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var etConfirmarContraseña: EditText
    private lateinit var btnRegistrar: Button
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_uactivity)

        txtIni = findViewById(R.id.txtIni)
        etNombre = findViewById(R.id.editText)
        etMarca = findViewById(R.id.editText2)
        etModelo = findViewById(R.id.editText3)
        etCorreo = findViewById(R.id.editTextTextEmailAddress)
        etContraseña = findViewById(R.id.editTextTextPassword)
        etConfirmarContraseña = findViewById(R.id.editTextPassword)
        btnRegistrar = findViewById(R.id.button)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        txtIni.setOnClickListener { Ini() }
        btnRegistrar.setOnClickListener { registrarUsuario() }
    }

    private fun Ini() {
        val reg = Intent(this, LoginUActivity::class.java)
        startActivity(reg)
        finish() // Cierra la actividad actual después de iniciar sesión
    }

    private fun registrarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val marca = etMarca.text.toString().trim()
        val modelo = etModelo.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()
        val confirmarContraseña = etConfirmarContraseña.text.toString().trim()

        if (nombre.isEmpty() || marca.isEmpty() || modelo.isEmpty() || correo.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contraseña != confirmarContraseña) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Register the user
        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User is registered successfully
                    val user = hashMapOf(
                        "nombre" to nombre,
                        "marca" to marca,
                        "modelo" to modelo,
                        "correo" to correo,
                        "contraseña" to contraseña,
                        "usuario" to "usuario" // Make sure this field is "usuario" for normal users and something different for mechanics
                    )

                    // Save the user object in Firestore
                    db.collection("usuarios") // Make sure you're saving in the correct collection
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Usuario almacenado con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                            // Redirect the user to the login activity
                            val intent = Intent(this, LoginUActivity::class.java)
                            startActivity(intent)
                            finish() // Close the current activity so they can't go back
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al almacenar usuario", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}