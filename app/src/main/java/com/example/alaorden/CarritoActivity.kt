package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class CarritoActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private lateinit var totalText: TextView
    private lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recycler = findViewById(R.id.recyclerCarrito)
        totalText = findViewById(R.id.txtTotal)
        bottomNav = findViewById(R.id.bottom_navigation)
        recycler.layoutManager = LinearLayoutManager(this)
        val btnPagar = findViewById<Button>(R.id.btnPagar)

        // Obtener carrito actual
        val productos = CarritoManager.obtenerCarrito().toMutableList()

        // Adapter con callback que actualiza el total en tiempo real
        adapter = CarritoAdapter(productos) { lista, total ->
            totalText.text = "Total: S/. ${"%.2f".format(total)}"
        }
        recycler.adapter = adapter

        // Mostrar el total inicial
        calcularTotal(productos)



        // Navegación inferior
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_carrito -> true // ya estamos aquí
                R.id.nav_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        // Seleccionar el item actual en la barra
        bottomNav.selectedItemId = R.id.nav_carrito

        btnPagar.setOnClickListener {
            val lista = CarritoManager.obtenerCarrito()
            val total = lista.sumOf { (it.precio ?: 0.0) * it.cantidad }

            val intent = Intent(this, MetodoPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", total) // Enviar el total al layout de pago
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        // refrescar desde manager por si hubo cambios
        val lista = CarritoManager.obtenerCarrito().toMutableList()
        adapter.updateList(lista)
        calcularTotal(lista)
    }

    private fun calcularTotal(lista: List<Producto>) {
        val total = lista.sumOf { (it.precio ?: 0.0) * it.cantidad }
        totalText.text = "Total: S/. ${"%.2f".format(total)}"
    }
}


