package com.secaac.mimec.perfil

import android.app.Activity
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.secaac.mimec.MainActivity
import com.secaac.mimec.R
import com.secaac.mimec.RcontrasenaU
import java.io.IOException
import java.util.UUID

class PerfilU : Fragment() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var nombre: TextView
    private lateinit var correo: TextView
    private lateinit var marca: TextView
    private lateinit var modelo: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_u, container, false)

        val user = FirebaseAuth.getInstance().currentUser
        val imageView4 = view.findViewById<ImageView>(R.id.imageView4)
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




    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null )
        {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                val imageView = view?.findViewById<ImageView>(R.id.imageView4)
                imageView?.setImageBitmap(bitmap)
                uploadImage()
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

private fun cargarDatosUsuario() {
    val db = Firebase.firestore

    // Obtener el ID del usuario actualmente autenticado
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        Log.d("PerfilU", "User ID: $userId") // Registrar el ID del usuario

        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("PerfilU", "Document data: ${document.data}") // Registrar los datos del documento

                    val nombreUsuario = document.getString("nombre")
                    val marcaUsuario = document.getString("marca")
                    val modeloUsuario = document.getString("modelo")
                    val correoUsuario = document.getString("correo")


                    activity?.runOnUiThread {
                        nombre.text = nombreUsuario
                        marca.text = marcaUsuario
                        modelo.text = modeloUsuario
                        correo.text = correoUsuario
                    }
                } else {
                    Log.d("PerfilU", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("PerfilU", "get failed with ", exception)
            }
    }
}

    private fun uploadImage() {
        if(filePath != null) {
            val ref = FirebaseStorage.getInstance().getReference("Fotos perfil usuario/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(it.metadata?.path))
                        .build()
                    user?.updateProfile(profileUpdates)
                }
                .addOnFailureListener { e ->
                    // Manejar cualquier error
                }
        }
    }


    private fun cerrarSesion() {
    FirebaseAuth.getInstance().signOut() // Cerrar la sesión del usuario actual
        val intent = Intent(activity, MainActivity::class.java)
        // Iniciar MainActivity
        startActivity(intent)
}


    private fun abrirRcontrasenaU() {
        val intent = Intent(activity, RcontrasenaU::class.java)
        startActivity(intent)
    }


}