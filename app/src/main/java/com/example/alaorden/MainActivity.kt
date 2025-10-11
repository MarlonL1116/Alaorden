package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var tvSaludo: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: EstablecimientoAdapter
    private lateinit var etSearch: EditText
    private val db = FirebaseFirestore.getInstance()

    private val listaEstablecimientos = mutableListOf<Establecimientos>()
    private val listaFiltrada = mutableListOf<Establecimientos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSaludo = findViewById(R.id.tvSaludo)
        recycler = findViewById(R.id.recyclerEstablecimientos)
        etSearch = findViewById(R.id.etSearch)

        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("name", "Usuario")
        tvSaludo.text = "Hola, $username 👋"

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = EstablecimientoAdapter(listaFiltrada) { est ->
            if (!est.id.isNullOrEmpty()) {
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

        // Cargar establecimientos desde Firestore
        cargarEstablecimientos()

        // 🔍 Buscador que filtra por nombre o productos
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarEstablecimientosYProductos(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Barra inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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
                R.id.nav_historial -> { // ✅ NUEVO
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

    // 🔹 Cargar todos los establecimientos
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
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔹 Filtro por tipo (categoría)
    private fun filtrarPorCategoria(tipo: String) {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos.filter { it.type.equals(tipo, ignoreCase = true) })
        adapter.updateList(listaFiltrada)
    }

    // 🔹 Buscador que busca en nombres y productos
    private fun buscarEstablecimientosYProductos(query: String) {
        if (query.isEmpty()) {
            mostrarTodos()
            return
        }

        val coincidencias = mutableListOf<Establecimientos>()

        // 1️⃣ Coincidencias por nombre del establecimiento
        coincidencias.addAll(
            listaEstablecimientos.filter {
                it.name.contains(query, ignoreCase = true)
            }
        )

        // 2️⃣ Buscar coincidencias dentro de los productos de cada establecimiento
        for (est in listaEstablecimientos) {
            if (est.id == null) continue
            db.collection("establecimientos")
                .document(est.id!!)
                .collection("productos")
                .get()
                .addOnSuccessListener { productos ->
                    for (doc in productos) {
                        val nombreProd = doc.getString("nombre") ?: ""
                        if (nombreProd.contains(query, ignoreCase = true)) {
                            if (!coincidencias.contains(est)) {
                                coincidencias.add(est)
                            }
                        }
                    }
                    listaFiltrada.clear()
                    listaFiltrada.addAll(coincidencias.distinct())
                    adapter.updateList(listaFiltrada)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error buscando productos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun mostrarTodos() {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos)
        adapter.updateList(listaFiltrada)
    }
}
