package com.example.alaorden

import com.google.firebase.Timestamp

data class Address(
    var id: String = "",
    var title: String = "",      // Ejemplo: "Casa", "Trabajo"
    var street: String = "",     // Dirección
    var notes: String? = "",     // Comentarios opcionales
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0,
    var createdAt: Timestamp? = null
)