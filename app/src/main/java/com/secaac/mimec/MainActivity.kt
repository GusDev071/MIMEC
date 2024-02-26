package com.secaac.mimec
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnU: Button
    private lateinit var btnM: Button

    // ID de permiso para la ubicación
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnU = findViewById(R.id.btnU)
        btnM = findViewById(R.id.btnM)

        btnU.setOnClickListener { login() }
        btnM.setOnClickListener { login2() }

        // Verificar si ya se han otorgado los permisos
        if (checkLocationPermission()) {
            // Los permisos de ubicación ya se han otorgado
            // Verificar si los permisos de la galería han sido otorgados
            if (!checkGalleryPermission()) {
                // Solicitar permisos de la galería si no se han otorgado
                requestGalleryPermission()
            }
        } else {
            // Solicitar permisos de ubicación si no se han otorgado
            requestLocationPermission()
        }
    }

    private fun login() {
        val login = Intent(this, LoginUActivity::class.java)
        startActivity(login)
        finish()
    }

    private fun login2() {
        val log2 = Intent(this, LoginMActivity::class.java)
        startActivity(log2)
        finish()
    }

    // Función para verificar si se han otorgado los permisos de ubicación
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Función para verificar si se han otorgado los permisos de la galería
    private fun checkGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Función para solicitar permisos de ubicación
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // Función para solicitar permisos de la galería
    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALLERY_PERMISSION_REQUEST_CODE
        )
    }

    // Método para manejar el resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de ubicación concedido, puedes realizar las acciones que lo requieran
                    // Verificar y solicitar permisos de la galería si no se han otorgado
                    if (!checkGalleryPermission()) {
                        requestGalleryPermission()
                    }
                } else {
                    // Permiso de ubicación denegado, debes manejar esto de acuerdo a tu lógica de la aplicación
                }
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de la galería concedido, puedes realizar las acciones que lo requieran
                } else {
                    // Permiso de la galería denegado, debes manejar esto de acuerdo a tu lógica de la aplicación
                }
            }
        }
    }
}
