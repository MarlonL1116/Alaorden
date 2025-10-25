package com.example.alaorden

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrdersActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val orders = mutableListOf<Order>()
    private lateinit var adapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerOrders)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = OrdersAdapter(orders)
        recycler.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        val uid = auth.currentUser?.uid ?: return

        // Escucha en tiempo real (si el estado cambia desde Firebase se actualiza en la app)
        db.collection("users").document(uid).collection("orders")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    orders.clear()
                    for (doc in snapshots.documents) {
                        val order = doc.toObject(Order::class.java)
                        if (order != null) {
                            order.id = doc.id
                            orders.add(order)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}
