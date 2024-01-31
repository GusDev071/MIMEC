package com.secaac.mimec

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class PerfilM : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_perfil_m, container, false)

        // Agregar OnClickListener al botón "Cerrar sesión"
        view.findViewById<Button>(R.id.cerrarSM).setOnClickListener {
            cerrarSesion()
        }

        // Agregar OnClickListener al TextView "Cambiar contraseña"
        view.findViewById<TextView>(R.id.textView8).setOnClickListener {
            // Lógica para iniciar la nueva actividad cuando se hace clic en el TextView
            iniciarNuevaActividad()
        }

        return view
    }

    // Función para cerrar la sesión
    private fun cerrarSesion() {
        // Crear un Intent para iniciar la actividad MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Iniciar la actividad MainActivity
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


