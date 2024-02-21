package com.secaac.mimec

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilM : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val vista = inflater.inflate(R.layout.fragment_perfil_m, container, false)

        // Obtener los datos del usuario de Firestore
        obtenerDatosUsuario(vista)

        // Agregar OnClickListener al botón "Cerrar sesión"
        vista.findViewById<Button>(R.id.cerrarSM).setOnClickListener {
            cerrarSesion()
        }

        // Agregar OnClickListener al TextView "Cambiar contraseña"
        vista.findViewById<TextView>(R.id.btn_cambiarc).setOnClickListener {
            // Lógica para iniciar la nueva actividad cuando se hace clic en el TextView
            iniciarNuevaActividad()
        }

        return vista
    }

   private fun obtenerDatosUsuario(vista: View) {
    // Recuperar el ID del documento de las preferencias compartidas
    val sharedPref = activity?.getSharedPreferences("myPrefs", MODE_PRIVATE)
    val userId = sharedPref?.getString("userId", null)

    if (userId != null) {
        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Encontrar los TextViews
                    val nombreMTextView = vista.findViewById<TextView>(R.id.nombre_m)
                    val correoMTextView = vista.findViewById<TextView>(R.id.correo_m)
                    val nombreTallerTextView = vista.findViewById<TextView>(R.id.n_taller)
                    val direccionTextView = vista.findViewById<TextView>(R.id.direccion)

                    // Establecer el texto de los TextViews con los datos del usuario
                    nombreMTextView.text = document.getString("nombreCompleto")
                    correoMTextView.text = document.getString("correo")
                    nombreTallerTextView.text = document.getString("nombreTaller")
                    // direccionTextView.text = document.getString("direccion") // No estás almacenando ninguna dirección en RegisterMActivity.kt
                } else {
                    Log.d("PerfilM", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("PerfilM", "get failed with ", exception)
            }
    }
}
    // Función para cerrar la sesión
    private fun cerrarSesion() {
        // Crear un Intent para iniciar MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Iniciar MainActivity
        startActivity(intent)
    }

    // Función para iniciar la nueva actividad
    private fun iniciarNuevaActividad() {
        // Crear un Intent para iniciar la nueva actividad (reemplaza NuevaActividad::class.java con el nombre real de tu nueva actividad)
        val intent = Intent(activity, Rcontrasena_M::class.java)
        // Iniciar la nueva actividad
        startActivity(intent)
    }
}