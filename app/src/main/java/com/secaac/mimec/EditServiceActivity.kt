package com.secaac.mimec

import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditServiceActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)

        // Retrieve the service from the Intent
        val serviceMap = intent.getSerializableExtra("service") as HashMap<String, Any>
        val service = Service().apply {
            id = serviceMap["id"] as String?
            serviceName = serviceMap["serviceName"] as String?
            cost = serviceMap["cost"] as String?
            serviceType = serviceMap["serviceType"] as String?
            description = serviceMap["description"] as String?
        }

        // Get references to the edit fields
        val serviceNameField = findViewById<TextInputEditText>(R.id.service_name)
        val costField = findViewById<TextInputEditText>(R.id.cost)
        val serviceTypeField = findViewById<AutoCompleteTextView>(R.id.service_type)
        val descriptionField = findViewById<TextInputEditText>(R.id.description)

        // Populate the edit fields with the current service data
        serviceNameField.setText(service.serviceName)
        costField.setText(service.cost)
        serviceTypeField.setText(service.serviceType)
        descriptionField.setText(service.description)

        // Get reference to the submit button
        val submitButton = findViewById<Button>(R.id.submit_button)

        // Set an OnClickListener for the submit button
        submitButton.setOnClickListener {
            // Get the new data from the edit fields
            service.serviceName = serviceNameField.text.toString()
            service.cost = costField.text.toString()
            service.serviceType = serviceTypeField.text.toString()
            service.description = descriptionField.text.toString()

            // Retrieve the user ID from 'sesionUsuario' collection
            db.collection("sesionUsuario")
                .document("sesionActual")
                .get()
                .addOnSuccessListener { document ->
                    val userId = document.getString("id")

                    // Check if userId is not null or empty
                    if (!userId.isNullOrEmpty()) {
                        // Update the service object in the database
                        db.collection("usuarios").document(userId).collection("services")
                            .document(service.id!!)
                            .set(service)
                            .addOnSuccessListener {
                                Toast.makeText(this@EditServiceActivity, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show()

                                // Create an Intent to start InicioM activity
                                val intent = Intent(this@EditServiceActivity, inicioM::class.java)

                                // Start the InicioM activity
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@EditServiceActivity, "Error en actualizar el servicio", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Show an error message
                        Toast.makeText(this@EditServiceActivity, "Error: ID del usuario inválido", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@EditServiceActivity, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
                }
        }

        // Get reference to the back button
        val backButton = findViewById<Button>(R.id.back_button)

        // Set an OnClickListener for the back button
        backButton.setOnClickListener {
            // Create an Intent to start InicioM activity
            val intent = Intent(this, inicioM::class.java)

            // Start the InicioM activity
            startActivity(intent)
        }
    }
}