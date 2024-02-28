package com.secaac.mimec.perfil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.secaac.mimec.MainActivity
import com.secaac.mimec.R
import com.secaac.mimec.Rcontrasena_M
import java.util.UUID

class PerfilM : Fragment() {

    private val db = Firebase.firestore
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var imageView5: ImageView
    private lateinit var etNombreCompleto: TextView
    private lateinit var etNombreTaller: TextView
    private lateinit var etCorreo: TextView
    private lateinit var etDireccion: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_perfil_m, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        imageView5 = vista.findViewById(R.id.imageView5)
        etNombreCompleto = vista.findViewById(R.id.nombre_m)
        etNombreTaller = vista.findViewById(R.id.n_taller)
        etCorreo = vista.findViewById(R.id.correo_m)
        etDireccion = vista.findViewById(R.id.direccioon)

        Glide.with(this)
            .load(user?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView5)

        imageView5.setOnClickListener {
            launchGallery()
        }

        vista.findViewById<Button>(R.id.cerrarSM).setOnClickListener {
            cerrarSesion()
        }

        vista.findViewById<TextView>(R.id.btn_cambiarc).setOnClickListener {
            iniciarNuevaActividad()
        }

        cargarDatosMecanico()

        return vista
    }

    override fun onResume() {
    super.onResume()
    // Obtener la URL de la imagen desde Firestore
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val photoUrl = document.getString("photoUrl")
                    // Utilizar Glide para cargar la imagen
                    Glide.with(this)
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView5)
                }
            }
    }
}

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null )
        {
            val tempUri = data.data
            filePath = copyImageToAppStorage(tempUri)
            try {
                // Utilizar Glide para cargar la imagen
                Glide.with(this)
                    .load(filePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView5)
                uploadImage()
            }
            catch (e: Exception){
                e.printStackTrace()
                // Aquí puedes manejar el error, por ejemplo, mostrando un mensaje al usuario
            }
        }
    }
    private fun copyImageToAppStorage(uri: Uri?): Uri? {
        val inputStream = activity?.contentResolver?.openInputStream(uri!!)
        val outputStream = activity?.openFileOutput("profile_picture.jpg", Context.MODE_PRIVATE)
        if (outputStream != null) {
            inputStream?.copyTo(outputStream)
        }
        inputStream?.close()
        outputStream?.close()
        return Uri.fromFile(activity?.getFileStreamPath("profile_picture.jpg"))
    }

  private fun cargarDatosMecanico() {
    val db = Firebase.firestore

    // Obtener el ID del usuario actualmente autenticado
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        Log.d("PerfilM", "User ID: $userId") // Registrar el ID del usuario

        db.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("PerfilM", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("PerfilM", "Current data: ${snapshot.data}") // Registrar los datos del documento

                    val nombreCompleto = snapshot.getString("nombreCompleto")
                    val nombreTaller = snapshot.getString("nombreTaller")
                    val correo = snapshot.getString("correo")
                    val direccion = snapshot.getString("direccion")
                    val photoUrl = snapshot.getString("photoUrl")

                    if (nombreCompleto == null || nombreTaller == null || correo == null || direccion == null || photoUrl == null) {
                        Log.d("PerfilM", "One or more fields are null")
                    }

                    activity?.runOnUiThread {
                        etNombreCompleto.text = nombreCompleto
                        etNombreTaller.text = nombreTaller
                        etCorreo.text = correo
                        etDireccion.text = direccion

                        // Cargar la imagen desde la URL
                        Glide.with(this)
                            .load(photoUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView5)
                    }
                } else {
                    Log.d("PerfilM", "Current data: null")
                }
            }
    } else {
        Log.d("PerfilM", "User ID is null")
    }
}

    private fun uploadImage() {
        if(filePath != null) {
            val db = Firebase.firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val oldImageUrl = document.getString("photoUrl")
                            if (oldImageUrl != null) {
                                val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
                                oldImageRef.delete()
                            }
                        }
                    }
            }

            val ref = FirebaseStorage.getInstance().getReference("Fotos perfil usuario/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build()
                        user?.updateProfile(profileUpdates)

                        if (userId != null) {
                            db.collection("usuarios").document(userId)
                                .update("photoUrl", uri.toString())
                        }

                        Glide.with(this)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView5)
                    }
                }
                .addOnFailureListener { e ->
                }
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut() // Cerrar la sesión del usuario actual
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun iniciarNuevaActividad() {
        val intent = Intent(activity, Rcontrasena_M::class.java)
        startActivity(intent)
    }
}