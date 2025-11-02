package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class PedidosActivity : AppCompatActivity() {
    private lateinit var recyclerPedidos: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter // ¡Necesitarás crear este adaptador!
    private val ordersList = mutableListOf<Order>() // Tu clase de modelo 'Order'

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)
        // Encuentra tu layout de encabezado
        val headerLayout = findViewById<LinearLayout>(R.id.header_orders) // Usa el ID de tu header
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBarInsets.top)
            WindowInsetsCompat.CONSUMED
        }

        // 1. Configurar RecyclerView
        recyclerPedidos = findViewById(R.id.recyclerPedidos)
        ordersAdapter = OrdersAdapter(ordersList) // Pasa la lista al adaptador
        recyclerPedidos.adapter = ordersAdapter
        recyclerPedidos.layoutManager = LinearLayoutManager(this)

        // 2. Cargar los pedidos desde Firebase
        loadOrdersFromFirebase()

        // 3. Configurar la barra de navegación (¡Importante!)
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
    }
    private fun loadOrdersFromFirebase() {
        val uid = auth.currentUser?.uid ?: return
        // 🔹 Este es tu NUEVO listener
        // Escucha en tiempo real (si el estado cambia desde Firebase se actualiza en la app)
        db.collection("users").document(uid).collection("orders")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    ordersList.clear()
                    for (doc in snapshots.documents) {
                        val order = doc.toObject(Order::class.java)
                        if (order != null) {
                            order.id = doc.id
                            ordersList.add(order)
                        }
                    }
                    ordersAdapter.notifyDataSetChanged()
                }
            }
    }
}