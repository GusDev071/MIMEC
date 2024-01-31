package com.secaac.mimec

import com.google.firebase.firestore.FirebaseFirestore
import com.secaac.mimec.Service

interface FirestoreCallback {
    fun onCallback(success: Boolean)
}

class DatabaseHelper {

    private val db = FirebaseFirestore.getInstance()

    fun updateService(serviceId: String, service: Service, callback: FirestoreCallback) {
        // Convertir el objeto Service a un Map
        val serviceMap = service.toMap()

        // Actualizar el documento en Firestore
        db.collection("services")
            .document(serviceId)
            .set(serviceMap)
            .addOnSuccessListener {
                // Llamar al callback con true para indicar que la operación fue exitosa
                callback.onCallback(true)
            }
            .addOnFailureListener { e ->
                // Llamar al callback con false para indicar que la operación falló
                callback.onCallback(false)
            }
    }
}