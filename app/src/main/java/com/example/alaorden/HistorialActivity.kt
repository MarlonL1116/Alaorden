package com.example.alaorden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    }

    private fun cargarHistorial() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("historial")
            .document(userId)
            .collection("pedidos")
            .orderBy("fecha")
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
    }
}
