package com.secaac.mimec

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

class LoginUActivity : AppCompatActivity() {
    private lateinit var txtRegistro: TextView
    private lateinit var txtReg3: TextView
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_uactivity)

        txtRegistro = findViewById(R.id.txtRegistro)
        txtReg3 = findViewById(R.id.txtReg3)
        etCorreo = findViewById(R.id.editTextTextEmailAddress)
        etContraseña = findViewById(R.id.editTextTextPassword)
        btnIniciarSesion = findViewById(R.id.button)
        firebaseAuth = FirebaseAuth.getInstance()

        txtRegistro.setOnClickListener { Registro() }

        txtReg3.setOnClickListener {
            val intentRecuperar = Intent(this, RecuperarContra::class.java)
            startActivity(intentRecuperar)
        }

        btnIniciarSesion.setOnClickListener { iniciarSesion() }
    }

    private fun Registro() {
        val reg = Intent(this, RegisterUActivity::class.java)
        startActivity(reg)
    }

    private fun iniciarSesion() {
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        db.collection("Usuarios").document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val userType = document.getString("userType")
                                if (userType == "user") {
                                    val intent = Intent(this, InicioUsuario ::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "No tienes permiso para acceder a esta sección", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
    }
}