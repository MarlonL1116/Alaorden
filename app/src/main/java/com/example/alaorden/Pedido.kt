package com.example.alaorden

data class Pedido(
    var id: String = "",
    val idUsuario: String = "",
    val establecimientoId: String = "",
    val nombreEstablecimiento: String = "",
    val total: Double = 0.0,
    val fecha: Long = System.currentTimeMillis(),
    val productos: List<Producto> = emptyList()
)