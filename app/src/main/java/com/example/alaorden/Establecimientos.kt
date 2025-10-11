package com.example.alaorden

data class Establecimientos(
    var id: String = "",
    val name: String = "",
    val type: String = "",
    val imageUrl: String? = "",
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0
)
