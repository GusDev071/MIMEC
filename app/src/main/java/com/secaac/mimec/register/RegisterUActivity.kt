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

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Crear un objeto usuario
                    val user = hashMapOf(
                        "nombre" to nombre,
                        "marca" to marca,
                        "modelo" to modelo,
                        "correo" to correo,
                        "usuario" to "usuario" // Asegúrate de que este campo sea "usuario" para los usuarios normales y algo diferente para los mecánicos
                    )

                    // Guardar el objeto usuario en Firestore
                    db.collection("usuarios") // Asegúrate de que estás guardando en la colección correcta
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Usuario almacenado con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()

                            // Redirigir al usuario a la actividad de inicio de sesión
                            val intent = Intent(this, LoginUActivity::class.java)
                            startActivity(intent)
                            finish() // Cierra la actividad actual para que no pueda volver atrás
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al almacenar usuario", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Si la creación de la cuenta falla, muestra un mensaje al usuario.
                    Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
                }
            }
    }
}