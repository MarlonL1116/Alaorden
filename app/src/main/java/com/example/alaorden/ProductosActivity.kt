package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosAdapter
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var txtTotal: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        recyclerView = findViewById(R.id.recyclerProductos)
        bottomNav = findViewById(R.id.bottom_navigation)
        txtTotal = findViewById(R.id.txtTotal)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adaptador con callback que actualiza el total
        productosAdapter = ProductosAdapter(mutableListOf()) { carrito, total ->
            txtTotal.text = "Total: S/. ${"%.2f".format(total)}"
        }
        recyclerView.adapter = productosAdapter

        val establecimientoId = intent.getStringExtra("ESTABLECIMIENTO_ID")
        if (establecimientoId != null) {
            cargarProductos(establecimientoId)
        } else {
            Toast.makeText(this, "Error: establecimiento no encontrado", Toast.LENGTH_SHORT).show()
        }

        // Barra inferior
        bottomNav.selectedItemId = R.id.nav_inicio
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_carrito -> {
                    startActivity(Intent(this, CarritoActivity::class.java))
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }



        // Mostrar total inicial
        actualizarTotal()
    }

    override fun onResume() {
        super.onResume()
        actualizarTotal()
    }

    private fun cargarProductos(establecimientoId: String) {
        Log.d("ProductosActivity", "Cargando productos para establecimiento: $establecimientoId")

        db.collection("establecimientos")
            .document(establecimientoId)
            .collection("productos")
            .get()
            .addOnSuccessListener { result ->
                val productosTemp = mutableListOf<Producto>() // ✅ lista temporal

                Log.d("ProductosActivity", "Productos encontrados: ${result.size()}")

                for (doc in result) {
                    try {
                        val producto = doc.toObject(Producto::class.java)
                        producto.id = doc.id
                        producto.idEstablecimiento = establecimientoId
                        productosTemp.add(producto)
                    } catch (e: Exception) {
                        Log.e("ProductosActivity", "Error al convertir producto: ${doc.id}", e)
                    }
                }

                Log.d("ProductosActivity", "Lista temporal de productos: ${productosTemp.size}")
                productosAdapter.updateList(productosTemp) // ✅ sin duplicados ni desaparición
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                Log.e("ProductosActivity", "Error Firebase", e)
            }
    }

    private fun actualizarTotal() {
        val total = CarritoManager.obtenerTotal()
        txtTotal.text = "Total: S/. ${"%.2f".format(total)}"
    }
}
