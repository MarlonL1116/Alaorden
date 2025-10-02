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

        // 🔹 Click abre ProductosActivity y pasa el ID del establecimiento
        adapter = EstablecimientoAdapter(listaFiltrada) { est ->
            if (!est.id.isNullOrEmpty()) {
                val intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("ESTABLECIMIENTO_ID", est.id) // ✅ usamos la misma clave
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

        // Cargar datos desde Firebase
        cargarEstablecimientos()

        // 🔍 Buscador en tiempo real
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarPorBusqueda(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Barra de navegación inferior
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_carrito -> {
                    val intent = Intent(this, CarritoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarEstablecimientos() {
        db.collection("establecimientos")
            .get()
            .addOnSuccessListener { result ->
                listaEstablecimientos.clear()
                for (doc in result) {
                    // ✅ Guardamos el ID del documento Firestore
                    val est = doc.toObject(Establecimientos::class.java).apply { id = doc.id }
                    listaEstablecimientos.add(est)
                }
                mostrarTodos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filtrarPorCategoria(tipo: String) {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos.filter { it.type.equals(tipo, ignoreCase = true) })
        adapter.updateList(listaFiltrada)
    }

    private fun filtrarPorBusqueda(query: String) {
        val queryLower = query.lowercase()
        val filtrados = if (queryLower.isEmpty()) {
            listaEstablecimientos
        } else {
            listaEstablecimientos.filter {
                it.name.lowercase().contains(queryLower)
            }
        }
        listaFiltrada.clear()
        listaFiltrada.addAll(filtrados)
        adapter.updateList(listaFiltrada)
    }

    private fun mostrarTodos() {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos)
        adapter.updateList(listaFiltrada)
    }
}
