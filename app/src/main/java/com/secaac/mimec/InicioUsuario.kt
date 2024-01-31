package com.secaac.mimec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.secaac.mimec.databinding.ActivityInicioMBinding
import com.secaac.mimec.databinding.ActivityInicioUsuarioBinding

class InicioUsuario : AppCompatActivity() {
    private lateinit var binding : ActivityInicioUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si el usuario está registrado
        if (!isUserRegistered()) {
            // Redirigir al usuario a la pantalla de inicio de sesión
            redirectToLoginScreen()
            return
        }

        replaceFragment(InicioUsuario2())

        binding.bottomNavigationView2.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home2-> replaceFragment(InicioUsuario2())
                R.id.mapa-> replaceFragment(MapaM())
                R.id.profileU -> replaceFragment(PerfilU())

                else->{}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout2,fragment)
        fragmentTransaction.commit()
    }

    // Sobrescribir onBackPressed para bloquear la salida
    override fun onBackPressed() {
        // No hacer nada
    }

    // Método para permitir salir de la actividad
    fun allowExit() {
        finish()
    }

    // Método para verificar si el usuario está registrado
    private fun isUserRegistered(): Boolean {
        // Implementar la lógica para verificar si el usuario está registrado
        return true
    }

    // Método para redirigir al usuario a la pantalla de inicio de sesión
    private fun redirectToLoginScreen() {
        // Implementar la lógica para redirigir al usuario a la pantalla de inicio de sesión
    }
}