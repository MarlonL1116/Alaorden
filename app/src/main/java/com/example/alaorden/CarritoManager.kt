package com.example.alaorden

object CarritoManager {
    private val carrito = mutableListOf<Producto>()

    /** Agregar un producto al carrito (si ya existe, aumenta la cantidad) */
    fun agregarProducto(producto: Producto) {
        val existente = carrito.find { it.id == producto.id }
        if (existente != null) {
            existente.cantidad += 1
        } else {
            // copiamos con cantidad = 1
            val nuevo = producto.copy(cantidad = 1)
            carrito.add(nuevo)
        }
    }

    /** Quitar un producto del carrito (si cantidad llega a 0, se elimina) */
    fun quitarProducto(producto: Producto) {
        val existente = carrito.find { it.id == producto.id }
        if (existente != null) {
            existente.cantidad -= 1
            if (existente.cantidad <= 0) {
                carrito.remove(existente)
            }
        }
    }

    /** Obtener una copia del carrito actual */
    fun obtenerCarrito(): List<Producto> {
        return carrito.toList()
    }

    /** Vaciar el carrito por completo */
    fun limpiarCarrito() {
        carrito.clear()
    }

    /** Calcular el total */
    fun obtenerTotal(): Double {
        return carrito.sumOf { it.precio * it.cantidad }
    }
}
