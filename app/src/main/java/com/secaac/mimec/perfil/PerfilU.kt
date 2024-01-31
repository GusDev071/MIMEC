package com.secaac.mimec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*

class PerfilU : Fragment() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_u, container, false)

        val user = FirebaseAuth.getInstance().currentUser

        val textView3 = view.findViewById<TextView>(R.id.textView3)
        val textView4 = view.findViewById<TextView>(R.id.textView4)
        val imageView4 = view.findViewById<ImageView>(R.id.imageView4)

        textView3.text = user?.displayName
        textView4.text = user?.email

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
        (activity as InicioUsuario).allowExit()
    }

    private fun abrirRcontrasenaU() {
        val intent = Intent(activity, RcontrasenaU::class.java)
        startActivity(intent)
    }
}