package com.secaac.mimec

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
    private var correoUsuarioActual = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val vista = inflater.inflate(R.layout.fragment_perfil_m, container, false)

        // Obtener el correo electrónico del usuario actual
        obtenerCorreoUsuarioActual(vista)

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

    private fun obtenerCorreoUsuarioActual(vista: View) {
        db.collection("sesionUsuario")
            .document("sesionActual")
            .get()
            .addOnSuccessListener { documento ->
                correoUsuarioActual = documento.getString("correo") ?: ""
                // Ahora que tenemos el correo del usuario, obtenemos sus datos
                obtenerDatosUsuario(vista)
            }
            .addOnFailureListener { excepcion ->
                Log.d("PerfilM", "falló la obtención con ", excepcion)
            }
    }

    private fun obtenerDatosUsuario(vista: View) {
        db.collection("usuarios")
            .whereEqualTo("correo", correoUsuarioActual)
            .get()
            .addOnSuccessListener { documentos ->
                for (documento in documentos) {
                    // Encontrar los TextViews
                    val nombreMTextView = vista.findViewById<TextView>(R.id.nombre_m)
                    val correoMTextView = vista.findViewById<TextView>(R.id.correo_m)
                    val nombreTallerTextView = vista.findViewById<TextView>(R.id.n_taller)
                    val direccionTextView = vista.findViewById<TextView>(R.id.direccion)

                    // Establecer el texto de los TextViews con los datos del usuario
                    nombreMTextView.text = documento.getString("nombreCompleto")
                    correoMTextView.text = correoUsuarioActual
                    nombreTallerTextView.text = documento.getString("nombreTaller")
                    direccionTextView.text = documento.getString("direccion")
                }
            }
            .addOnFailureListener { excepcion ->
                Log.d("PerfilM", "falló la obtención con ", excepcion)
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