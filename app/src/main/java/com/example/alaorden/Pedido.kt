package com.example.alaorden

import com.google.firebase.Timestamp

data class Pedido(
    var id: String = "",
    val idUsuario: String = "",
    val establecimientoId: String = "",
    val nombreEstablecimiento: String = "",
    val total: Double = 0.0,
    val fecha: Any? = null, // 🔹 Puede ser Timestamp o Long
    val productos: List<Producto> = emptyList(),
    val status: String = "received"

) {
    fun obtenerFechaTimestamp(): Timestamp {
        return when (fecha) {
            is Timestamp -> fecha as Timestamp
            is Long -> Timestamp(fecha as Long / 1000, 0)
            else -> Timestamp.now()
        }
    }
}
