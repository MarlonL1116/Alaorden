package com.example.alaorden

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CarritoActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private lateinit var totalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recycler = findViewById(R.id.recyclerCarrito)
        totalText = findViewById(R.id.txtTotal)

        recycler.layoutManager = LinearLayoutManager(this)

        // Obtenemos productos del carrito, si es null se inicializa vacía
        val productos = CarritoManager.obtenerCarrito()?.toMutableList() ?: mutableListOf()

        // Configuramos adapter con callback para actualizar total
        adapter = CarritoAdapter(productos) { lista, _ ->
            calcularTotal(lista)
        }

        recycler.adapter = adapter

        // Total inicial
        calcularTotal(productos)
    }

    private fun calcularTotal(lista: List<Producto>) {
        // Evitamos null en precio o cantidad
        val total = lista.sumOf { (it.precio ?: 0.0) * (it.cantidad ?: 0) }
        totalText.text = "Total: S/. ${"%.2f".format(total)}"
    }

    override fun onResume() {
        super.onResume()
        // Refrescamos lista al volver al carrito
        val productosActualizados = CarritoManager.obtenerCarrito()?.toMutableList() ?: mutableListOf()
        adapter.updateList(productosActualizados)
        calcularTotal(productosActualizados)
    }
}
