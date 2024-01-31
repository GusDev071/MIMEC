package com.secaac.mimec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class agregar : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(Companion.ARG_PARAM1_AGGREGAR)
            param2 = it.getString(Companion.ARG_PARAM2_AGGREGAR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agregar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = resources.getStringArray(R.array.service_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        val dropdownMenu = view.findViewById<TextInputLayout>(R.id.service_type_layout)
        val autoCompleteTextView = dropdownMenu.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(adapter)

        val serviceName = view.findViewById<TextInputEditText>(R.id.service_name)
        val cost = view.findViewById<TextInputEditText>(R.id.cost)
        val description = view.findViewById<TextInputEditText>(R.id.description)
        val submitButton = view.findViewById<Button>(R.id.submit_button)

        submitButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()

            val serviceData = hashMapOf(
                "serviceName" to serviceName.text.toString(),
                "cost" to cost.text.toString(),
                "serviceType" to autoCompleteTextView?.text.toString(),
                "description" to description.text.toString()
            )

            val userRef = db.collection("users").document(userId!!).collection("services")
            val newServiceRef = userRef.document() // Create a new document with a unique ID

            serviceData["id"] = newServiceRef.id // Add the unique ID to the service data

            newServiceRef.set(serviceData) // Set the service data in the new document
                .addOnSuccessListener {
                    Toast.makeText(context, "Datos enviados con éxito", Toast.LENGTH_SHORT).show()
                    serviceName.text = null
                    cost.text = null
                    description.text = null
                    autoCompleteTextView?.setText("")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al enviar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        const val ARG_PARAM1_AGGREGAR = "param1_agregar"
        const val ARG_PARAM2_AGGREGAR = "param2_agregar"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            agregar().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1_AGGREGAR, param1)
                    putString(ARG_PARAM2_AGGREGAR, param2)
                }
            }
    }
}