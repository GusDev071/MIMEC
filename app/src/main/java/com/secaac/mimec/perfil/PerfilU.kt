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
import com.secaac.mimec.RcontrasenaU
import java.util.UUID

class PerfilU : Fragment() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var nombre: TextView
    private lateinit var correo: TextView
    private lateinit var marca: TextView
    private lateinit var modelo: TextView
    private lateinit var imageView4: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_u, container, false)

        val user = FirebaseAuth.getInstance().currentUser
        imageView4 = view.findViewById(R.id.imageView4)
        nombre = view.findViewById(R.id.textView3)
        correo = view.findViewById(R.id.textView4)
        marca = view.findViewById(R.id.marcaa)
        modelo = view.findViewById(R.id.modeloo)

        imageView4.setOnClickListener {
            launchGallery()
        }

        Glide.with(this)
            .load(user?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView4)

        view.findViewById<Button>(R.id.cerrarSU).setOnClickListener {
            cerrarSesion()
        }

        view.findViewById<TextView>(R.id.Cambiarcontra).setOnClickListener {
            abrirRcontrasenaU()
        }

        cargarDatosUsuario()
        return view
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
                        .into(imageView4)
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
                .into(imageView4)
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

    private fun cargarDatosUsuario() {
    val db = Firebase.firestore

    // Obtener el ID del usuario actualmente autenticado
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        Log.d("PerfilU", "User ID: $userId") // Registrar el ID del usuario

        db.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("PerfilU", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("PerfilU", "Current data: ${snapshot.data}") // Registrar los datos del documento

                    val nombreUsuario = snapshot.getString("nombre")
                    val marcaUsuario = snapshot.getString("marca")
                    val modeloUsuario = snapshot.getString("modelo")
                    val correoUsuario = snapshot.getString("correo")
                    val photoUrl = snapshot.getString("photoUrl")

                    activity?.runOnUiThread {
                        nombre.text = nombreUsuario
                        marca.text = marcaUsuario
                        modelo.text = modeloUsuario
                        correo.text = correoUsuario

                        // Cargar la imagen desde la URL
                        Glide.with(this)
                            .load(photoUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView4)
                    }
                } else {
                    Log.d("PerfilU", "Current data: null")
                }
            }
    }
}

    private fun uploadImage() {
    if(filePath != null) {
        // Obtener la referencia a la imagen anterior
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val oldImageUrl = document.getString("photoUrl")
                        if (oldImageUrl != null) {
                            // Crear una referencia a la imagen antigua
                            val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
                            // Eliminar la imagen antigua
                            oldImageRef.delete()
                        }
                    }
                }
        }

        // Subir la nueva imagen
        val ref = FirebaseStorage.getInstance().getReference("Fotos perfil usuario/" + UUID.randomUUID().toString())
        ref.putFile(filePath!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()
                    user?.updateProfile(profileUpdates)

                    // Guardar la URL de la imagen en Firestore
                    if (userId != null) {
                        db.collection("usuarios").document(userId)
                            .update("photoUrl", uri.toString())
                    }

                    // Actualizar la imagen en el ImageView
                    Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView4)
                }
            }
            .addOnFailureListener { e ->
                // Manejar cualquier error
            }
    }
}

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut() // Cerrar la sesión del usuario actual
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun abrirRcontrasenaU() {
        val intent = Intent(activity, RcontrasenaU::class.java)
        startActivity(intent)
    }
}
