package com.secaac.mimec

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore

class Servicios : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_servicios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ServiceAdapter(emptyList(), ::onEdit, ::onDelete, ::saveService)

        // Retrieve the user ID from 'sesionUsuario' collection
        db.collection("sesionUsuario")
            .document("sesionActual")
            .get()
            .addOnSuccessListener { document ->
                val userId = document.getString("id")

                // Check if userId is not null or empty
                if (!userId.isNullOrEmpty()) {
                    // Fetch the services associated with the current user
                    db.collection("services")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { result ->
                            val services = mutableListOf<Service>()
                            for (document in result) {
                                val service = document.toObject(Service::class.java)
                                services.add(service)
                            }
                            (recyclerView.adapter as ServiceAdapter).updateData(services)
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                        }
                } else {
                    // Show an error message
                    Toast.makeText(context, "Error: ID del usuario inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onEdit(service: Service) {
        val intent = Intent(context, EditServiceActivity::class.java)
        intent.putExtra("service", service)
        startActivity(intent)
    }

    private fun onDelete(service: Service) {
        // Retrieve the user ID from 'sesionUsuario' collection
        db.collection("sesionUsuario")
            .document("sesionActual")
            .get()
            .addOnSuccessListener { document ->
                val userId = document.getString("id")

                // Check if userId is not null or empty
                if (!userId.isNullOrEmpty()) {
                    // Delete the service from the database
                    db.collection("services")
                        .document(service.id!!)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Servicio eliminado con éxito", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error al eliminar el servicio", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Show an error message
                    Toast.makeText(context, "Error: ID del usuario inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveService(service: Service) {
        // Retrieve the user ID from 'sesionUsuario' collection
        db.collection("sesionUsuario")
            .document("sesionActual")
            .get()
            .addOnSuccessListener { document ->
                val userId = document.getString("id")

                // Check if userId is not null or empty
                if (!userId.isNullOrEmpty()) {
                    // Save the service to the database
                    db.collection("services").document(service.id!!)
                        .set(service)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Servicio guardado con éxito", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error al guardar el servicio", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Show an error message
                    Toast.makeText(context, "Error: ID del usuario inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Servicios().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}