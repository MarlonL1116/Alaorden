package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistorialAdapter
    private val pedidosList = mutableListOf<Pedido>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerView = findViewById(R.id.recyclerHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = HistorialAdapter(pedidosList)
        recyclerView.adapter = adapter

        cargarHistorial()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_carrito -> {
                    startActivity(Intent(this, CarritoActivity::class.java))
                    true
                }
                R.id.nav_historial -> {
                    // Ya estamos en historial
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // 🔹 Cargar el historial de pedidos del usuario
    private fun cargarHistorial() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("historial")
            .document(userId)
            .collection("pedidos")
            .orderBy("fecha") // 🔸 Campo que agregamos en ConfirmacionPagoActivity
            .get()
            .addOnSuccessListener { result ->
                pedidosList.clear()
                for (doc in result) {
                    val pedido = doc.toObject(Pedido::class.java)
                    pedido.id = doc.id
                    pedidosList.add(pedido)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // puedes agregar un Toast si deseas avisar que no cargó
            }
    }
}
