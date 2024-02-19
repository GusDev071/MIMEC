package com.secaac.mimec

import com.google.firebase.firestore.FirebaseFirestore
import com.secaac.mimec.Service

interface FirestoreCallback {
    fun onCallback(success: Boolean)
}

class DatabaseHelper {

    private val db = FirebaseFirestore.getInstance()

    fun updateService(service: Service, callback: FirestoreCallback) {
        // Convertir el objeto Service a un Map
        val serviceMap = service.toMap()

        // Obtener el ID del servicio
        val serviceId = service.id

        // Verificar si el ID del servicio no es nulo
        if (serviceId != null) {
            // Obtener la referencia al documento
            val docRef = db.collection("services").document(serviceId)

            // Verificar si el documento existe
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    // Actualizar el documento en Firestore
                    docRef.update(serviceMap)
                        .addOnSuccessListener {
                            // Llamar al callback con true para indicar que la operación fue exitosa
                            callback.onCallback(true)
                        }
                        .addOnFailureListener { e ->
                            // Llamar al callback con false para indicar que la operación falló
                            callback.onCallback(false)
                        }
                } else {
                    // Llamar al callback con false para indicar que el documento no existe
                    callback.onCallback(false)
                }
            }
        } else {
            // Llamar al callback con false para indicar que el ID del servicio es nulo
            callback.onCallback(false)
        }
    }
}