package com.secaac.mimec

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
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

    FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contraseña)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                Log.d("LoginMActivity", "User ID: $userId") // Imprimir el ID del usuario en el logcat

                val intent = Intent(this, inicioM::class.java)
                startActivity(intent)
                finish()
            } else {
                // Si el inicio de sesión falla, muestra un mensaje al usuario.
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        }
}


}