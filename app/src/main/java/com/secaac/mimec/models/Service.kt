package com.secaac.mimec

import java.io.Serializable

data class Service(
    var id: String? = null,
    var serviceName: String? = null,
    var cost: String? = null,
    var serviceType: String? = null,
    var description: String? = null
) : Serializable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "serviceName" to serviceName,
            "cost" to cost,
            "serviceType" to serviceType,
            "description" to description
        )
    }
}