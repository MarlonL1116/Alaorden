package com.example.alaorden

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle

import android.widget.LinearLayout
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: EstablecimientoAdapter
    private val db = FirebaseFirestore.getInstance()

    private val listaEstablecimientos = mutableListOf<Establecimientos>()
    private val listaFiltrada = mutableListOf<Establecimientos>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", null) ?: "Usuario"

        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        tvSaludo.text = "Hola, $username 👋"


        recycler = findViewById(R.id.recyclerEstablecimientos)
        recycler.layoutManager = LinearLayoutManager(this)

        // 🔹 Click abre ProductosActivity y pasa el ID del establecimiento
        adapter = EstablecimientoAdapter(listaFiltrada) { est ->
            if (!est.id.isNullOrEmpty()) {
                val intent = Intent(this, ProductosActivity::class.java)
                intent.putExtra("ESTABLECIMIENTO_ID", est.id) // ✅ usamos misma clave que en ProductosActivity
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

        btnRestaurantes.setOnClickListener { filtrarLista("restaurante") }
        btnTiendas.setOnClickListener { filtrarLista("tienda") }
        btnFarmacias.setOnClickListener { filtrarLista("farmacia") }

        // Cargar datos desde Firebase
        cargarEstablecimientos()

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    // Ya estamos en MainActivity
                    true
                }
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
                    // ✅ Ahora guardamos el id del documento Firestore
                    val est = doc.toObject(Establecimientos::class.java).apply { id = doc.id }
                    listaEstablecimientos.add(est)
                }
                mostrarTodos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filtrarLista(tipo: String) {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos.filter { it.type.equals(tipo, ignoreCase = true) })
        adapter.updateList(listaFiltrada)
    }

    private fun mostrarTodos() {
        listaFiltrada.clear()
        listaFiltrada.addAll(listaEstablecimientos)
        adapter.updateList(listaFiltrada)
    }
}
