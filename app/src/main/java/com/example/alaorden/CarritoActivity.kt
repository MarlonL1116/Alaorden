package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class CarritoActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private lateinit var totalText: TextView
    private lateinit var bottomNav: BottomNavigationView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

        // 🔹 Confirmar pedido
        btnPagar.setOnClickListener {
            val lista = CarritoManager.obtenerCarrito()
            val total = lista.sumOf { (it.precio) * it.cantidad }

            if (lista.isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Solo navegar a la pantalla de método de pago
            val intent = Intent(this, MetodoPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", total)
            startActivity(intent)
        }


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
                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_carrito
    }

    override fun onResume() {
        super.onResume()
        val lista = CarritoManager.obtenerCarrito().toMutableList()
        adapter.updateList(lista)
        calcularTotal(lista)
    }

    private fun calcularTotal(lista: List<Producto>) {
        val total = lista.sumOf { it.precio * it.cantidad }
        totalText.text = "Total: S/. ${"%.2f".format(total)}"
    }
}
