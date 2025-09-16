package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var txtNombre: TextView
    private lateinit var txtTelefono: TextView
    private lateinit var txtCorreo: TextView
    private lateinit var bottomNav: BottomNavigationView

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        txtNombre = findViewById(R.id.txtNombre)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtCorreo = findViewById(R.id.txtCorreo)
        bottomNav = findViewById(R.id.bottom_navigation)

        txtNombre.text = "Nombre: —"
        txtTelefono.text = "Teléfono: —"
        txtCorreo.text = "Correo: ${auth.currentUser?.email ?: "—"}"

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val nombre = doc.getString("name") ?: ""
                        val telefono = doc.getString("phone") ?: ""
                        val correo = doc.getString("email") ?: auth.currentUser?.email ?: ""

                        if (nombre.isNotEmpty()) txtNombre.text = "Nombre: $nombre"
                        if (telefono.isNotEmpty()) txtTelefono.text = "Teléfono: $telefono"
                        txtCorreo.text = "Correo: $correo"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "No se pudo cargar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Seleccionar item
        bottomNav.selectedItemId = R.id.nav_perfil

        // Navegación
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_carrito -> {
                    val intent = Intent(this, CarritoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    true
                }
                else -> false
            }
        }
    }
}
