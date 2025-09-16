package com.example.alaorden

object CarritoManager {
    private var carrito: MutableList<Producto> = mutableListOf()
    private var establecimientoId: String? = null

    fun agregarProducto(producto: Producto): Boolean {
        // Si el carrito está vacío, guardamos el establecimiento
        if (carrito.isEmpty()) {
            establecimientoId = producto.idEstablecimiento
        }

        // Si es de otro establecimiento, no dejamos
        if (producto.idEstablecimiento != establecimientoId) {
            return false // 🚫 producto rechazado
        }

        // Buscar si ya existe
        val existente = carrito.find { it.id == producto.id }
        if (existente != null) {
            existente.cantidad += 1
        } else {
            carrito.add(producto.copy(cantidad = 1))
        }
        return true
    }

    // ✅ Modificado para devolver Boolean
    fun quitarProducto(producto: Producto): Boolean {
        val existente = carrito.find { it.id == producto.id }
        return if (existente != null) {
            existente.cantidad -= 1
            if (existente.cantidad <= 0) carrito.remove(existente)

            // Si el carrito quedó vacío, reseteamos el establecimiento
            if (carrito.isEmpty()) {
                establecimientoId = null
            }
            true
        } else {
            false
        }
    }

    fun obtenerCarrito(): List<Producto> = carrito

    fun vaciarCarrito() {
        carrito.clear()
        establecimientoId = null
    }

    fun obtenerEstablecimiento(): String? = establecimientoId

    fun obtenerTotal(): Double {
        return carrito.sumOf { it.precio * it.cantidad }
    }
}
