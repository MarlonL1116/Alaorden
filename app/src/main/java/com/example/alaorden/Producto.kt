package com.example.alaorden

data class Producto(
    var id: String? = "",
    var idEstablecimiento: String = "",
    var nombre: String? = "",
    var descripcion: String? = "",
    var precio: Double = 0.0,   // 👈 valor por defecto agregado
    var imageUrl: String? = "",
    var cantidad: Int = 0

)
