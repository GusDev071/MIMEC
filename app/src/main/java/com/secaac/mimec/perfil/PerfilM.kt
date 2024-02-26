package com.secaac.mimec.perfil

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secaac.mimec.MainActivity
import com.secaac.mimec.R
import com.secaac.mimec.Rcontrasena_M

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
    val db = Firebase.firestore

    // Recuperar el ID del documento de las preferencias compartidas
    val sharedPref = activity?.getSharedPreferences("myPrefs", MODE_PRIVATE)
    val mecanicoId = sharedPref?.getString("mecanicoId", null) // Usa "mecanicoId" para el mecánico

    if (mecanicoId != null) {
        db.collection("usuarios").document(mecanicoId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Encontrar los TextViews
                    val nombreMTextView = vista.findViewById<TextView>(R.id.nombre_m)
                    val correoMTextView = vista.findViewById<TextView>(R.id.correo_m)
                    val nombreTallerTextView = vista.findViewById<TextView>(R.id.n_taller)
                    val direccionTextView = vista.findViewById<TextView>(R.id.direccioon)

                    // Establecer el texto de los TextViews con los datos del usuario
                    nombreMTextView.text = document.getString("nombreCompleto")
                    correoMTextView.text = document.getString("correo")
                    nombreTallerTextView.text = document.getString("nombreTaller")
                    direccionTextView.text = document.getString("direccion")
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
    FirebaseAuth.getInstance().signOut() // Cerrar la sesión del usuario actual
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