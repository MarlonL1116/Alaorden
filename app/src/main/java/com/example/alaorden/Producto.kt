package com.example.alaorden

data class Producto(
    var id: String? = "",
    var idEstablecimiento: String = "",
    var nombreEstablecimiento: String = "",
    val nombre: String? = "",
    val descripcion: String? = "",
    val precio: Double = 0.0,
    val imageUrl: String? = "",
    var cantidad: Int = 0

)
