package com.example.alaorden

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosAdapter
    private val listaProductos = mutableListOf<Producto>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        recyclerView = findViewById(R.id.recyclerProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Usamos directamente la lista mutable
        productosAdapter = ProductosAdapter(listaProductos) { _, _ -> }
        recyclerView.adapter = productosAdapter

        val establecimientoId = intent.getStringExtra("ESTABLECIMIENTO_ID")

        if (establecimientoId != null) {
            cargarProductos(establecimientoId)
        } else {
            Toast.makeText(this, "Error: establecimiento no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarProductos(establecimientoId: String) {
        db.collection("establecimientos")
            .document(establecimientoId)
            .collection("productos")
            .get()
            .addOnSuccessListener { result ->
                listaProductos.clear()
                for (doc in result) {
                    try {
                        val producto = doc.toObject(Producto::class.java)
                        producto.id = doc.id
                        producto.idEstablecimiento = establecimientoId // 👈 Guardamos el id del establecimiento
                        listaProductos.add(producto)
                    } catch (e: Exception) {
                        Log.e("ProductosActivity", "Error al convertir producto: ${doc.id}", e)
                    }
                }
                productosAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                Log.e("ProductosActivity", "Error Firebase", e)
            }
    }
}
