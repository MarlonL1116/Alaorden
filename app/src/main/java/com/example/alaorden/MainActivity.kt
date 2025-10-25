package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var tvDireccionSeleccionada: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: EstablecimientoAdapter
    private lateinit var etSearch: EditText

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val listaEstablecimientos = mutableListOf<Establecimientos>()
    private val listaFiltrada = mutableListOf<Establecimientos>()
    private var searchJob: Job? = null // Job para el debounce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        // Referencias UI
        tvSaludo = findViewById(R.id.tvSaludo)
        tvDireccionSeleccionada = findViewById(R.id.tvDireccionSeleccionada)
        recycler = findViewById(R.id.recyclerEstablecimientos)
        etSearch = findViewById(R.id.etSearch)

        // 🔹 Configurar texto y listener
        tvSaludo.text = "Mis Direcciones"
        tvSaludo.setOnClickListener {
            val intent = Intent(this, AddressesActivity::class.java)
            intent.putExtra("SELECT_MODE", true) // modo selección
            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = EstablecimientoAdapter(listaFiltrada) { est ->
            if (est.id.isNotEmpty()) {
                val intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("ESTABLECIMIENTO_ID", est.id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Establecimiento sin ID", Toast.LENGTH_SHORT).show()
            }
        }
        recycler.adapter = adapter

        // Botones de filtro
        val btnRestaurantes = findViewById<LinearLayout>(R.id.btnRestaurantes)
        val btnTiendas = findViewById<LinearLayout>(R.id.btnTiendas)
        val btnFarmacias = findViewById<LinearLayout>(R.id.btnFarmacias)

        btnRestaurantes.setOnClickListener { filtrarPorCategoria("restaurante") }
        btnTiendas.setOnClickListener { filtrarPorCategoria("tienda") }
        btnFarmacias.setOnClickListener { filtrarPorCategoria("farmacia") }

        // 🔍 Buscador con debounce
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = MainScope().launch {
                    delay(500)
                    buscarEstablecimientosYProductos(s.toString().trim())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Barra inferior
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

        // ✅ Verificar si hay pedido activo antes de mostrar establecimientos
        checkActiveOrder()
        cargarEstablecimientos()
    }

    override fun onResume() {
        super.onResume()
        mostrarDireccionSeleccionada()
    }

    // ============================================================
    // 🔹 Muestra la dirección seleccionada
    // ============================================================
    private fun mostrarDireccionSeleccionada() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val addr = doc.get("selectedAddress") as? Map<*, *>
                if (addr != null) {
                    val title = addr["title"] ?: ""
                    val street = addr["street"] ?: ""
                    tvDireccionSeleccionada.text = "Enviar a: $title - $street"
                } else {
                    tvDireccionSeleccionada.text = "Enviar a: (ninguna seleccionada)"
                }
            }
            .addOnFailureListener {
                tvDireccionSeleccionada.text = "Error al cargar dirección"
            }
    }

    // ============================================================
    // ✅ Verificar pedido activo (TICKET)
    // ============================================================
    private fun checkActiveOrder() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // 🔹 Busca pedidos activos del usuario con estado received o in_transit
        db.collection("users").document(uid).collection("orders")
            .whereIn("status", listOf("received", "in_transit"))
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                val layoutPedidoActivo = findViewById<LinearLayout>(R.id.layoutPedidoActivo)

                if (snapshots != null && !snapshots.isEmpty) {
                    // ✅ Mostrar pedido activo
                    val pedidoDoc = snapshots.documents.first()
                    val pedido = pedidoDoc.toObject(Order::class.java)

                    layoutPedidoActivo.visibility = View.VISIBLE

                    findViewById<TextView>(R.id.tvPedidoTitulo).text = "Pedido en curso 🚚"
                    findViewById<TextView>(R.id.tvPedidoEstado).text =
                        "Estado: " + when (pedido?.status) {
                            "received" -> "Se está preparando su orden"
                            "in_transit" -> "En camino"
                            else -> "Desconocido"
                        }
                    findViewById<TextView>(R.id.tvPedidoEstablecimiento).text =
                        pedido?.establecimientoName ?: "Producto"
                    findViewById<TextView>(R.id.tvPedidoTotal).text =
                        "Total: S/%.2f".format(pedido?.total ?: 0.0)
                } else {
                    // 🔹 Si no hay pedido activo, ocultamos el bloque
                    layoutPedidoActivo.visibility = View.GONE
                }
            }
    }

    // ============================================================
    // 🔹 Cargar establecimientos desde Firestore
    // ============================================================
    private fun cargarEstablecimientos() {
        db.collection("establecimientos")
            .get()
            .addOnSuccessListener { result ->
                listaEstablecimientos.clear()
                for (doc in result) {
                    val est = doc.toObject(Establecimientos::class.java).apply { id = doc.id }
                    listaEstablecimientos.add(est)
                }
                mostrarTodos()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error al cargar establecimientos", e)
                Toast.makeText(
                    this,
                    "No se pudieron cargar los establecimientos. Inténtalo de nuevo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ============================================================
    // 🔹 Filtros y búsqueda
    // ============================================================
    private fun filtrarPorCategoria(tipo: String) {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos.filter { it.type.equals(tipo, ignoreCase = true) })
        adapter.updateList(listaFiltrada)
    }

    private fun buscarEstablecimientosYProductos(query: String) {
        if (query.isEmpty()) {
            mostrarTodos()
            return
        }

        val coincidencias = mutableListOf<Establecimientos>()
        coincidencias.addAll(listaEstablecimientos.filter { it.name.contains(query, ignoreCase = true) })

        for (est in listaEstablecimientos) {
            if (est.id.isEmpty()) continue
            db.collection("establecimientos")
                .document(est.id)
                .collection("productos")
                .get()
                .addOnSuccessListener { productos ->
                    for (doc in productos) {
                        val nombreProd = doc.getString("nombre") ?: ""
                        if (nombreProd.contains(query, ignoreCase = true)) {
                            if (!coincidencias.contains(est)) coincidencias.add(est)
                        }
                    }
                    listaFiltrada.clear()
                    listaFiltrada.addAll(coincidencias.distinct())
                    adapter.updateList(listaFiltrada)
                }
        }
    }

    private fun mostrarTodos() {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos)
        adapter.updateList(listaFiltrada)
    }
}
