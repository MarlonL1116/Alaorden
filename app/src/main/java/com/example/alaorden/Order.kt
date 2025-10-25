package com.example.alaorden

import com.google.firebase.Timestamp

data class OrderProduct(
    var id: String = "",
    var nombre: String? = "",
    var precio: Double = 0.0,
    var cantidad: Int = 0
)

data class OrderAddress(
    var title: String = "",
    var street: String = "",
    var notes: String? = "",
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0
)

data class Order(
    var id: String = "",
    var userId: String = "",
    var establecimientoId: String = "",
    var establecimientoName: String = "",
    var direccion: OrderAddress? = null,
    var productos: List<OrderProduct> = emptyList(),
    var total: Double = 0.0,
    var status: String = "received", // received, in_transit, delivered
    var createdAt: Timestamp? = null
)