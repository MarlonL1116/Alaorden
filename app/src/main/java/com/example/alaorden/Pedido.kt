package com.example.alaorden

data class Pedido(
    var id: String = "",
    var idUsuario: String = "",
    var establecimientoId: String = "",
    var nombreEstablecimiento: String = "",
    var total: Double = 0.0,
    var fecha: Long = System.currentTimeMillis(),
    var productos: List<Producto> = emptyList()
)