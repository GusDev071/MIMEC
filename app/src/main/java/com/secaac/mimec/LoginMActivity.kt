package com.secaac.mimec

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secaac.mimec.register.RegisterMActivity

class LoginMActivity : AppCompatActivity() {
    private lateinit var txtReg2: TextView
    private lateinit var txtReg3: TextView
    private lateinit var iniS: Button
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_mactivity)

        txtReg2 = findViewById(R.id.txtReg2)
        txtReg3 = findViewById(R.id.txtReg3)
        iniS = findViewById(R.id.iniS)
        etCorreo = findViewById(R.id.editTextTextEmailAddress)
        etContraseña = findViewById(R.id.editTextTextPassword)

        txtReg2.setOnClickListener { Reg2() }
        iniS.setOnClickListener { IniS() }
        txtReg3.setOnClickListener {
            val intent = Intent(this, RecuperarContra::class.java)
            startActivity(intent)
        }
    }

    private fun Reg2() {
        val ini = Intent(this, RegisterMActivity::class.java)
        startActivity(ini)
    }

    private fun IniS() {
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .whereEqualTo("contraseña", contraseña)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.getString("usuario") == "mechanic") {
                        // Inicio de sesión exitoso
                        val ini = Intent(this, inicioM::class.java)
                        startActivity(ini)
                        finish()
                    } else {
                        Toast.makeText(this, "No tienes permiso para acceder a esta sección", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
    }
}