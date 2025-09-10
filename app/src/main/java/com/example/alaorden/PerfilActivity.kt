package com.example.alaorden

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var txtNombre: TextView
    private lateinit var txtApellidos: TextView
    private lateinit var txtCorreo: TextView

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil) // asegúrate que este es el nombre correcto

        // enlazamos vistas
        txtNombre = findViewById(R.id.txtNombre)
        txtApellidos = findViewById(R.id.txtApellidos)
        txtCorreo = findViewById(R.id.txtCorreo)

        // valores por defecto
        txtNombre.text = "Nombre: —"
        txtApellidos.text = "Apellidos: —"
        txtCorreo.text = "Correo: ${auth.currentUser?.email ?: "—"}"

        // Si guardas perfil en Firestore (colección "users", documento = uid), lo cargamos
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val nombre = doc.getString("nombre") ?: ""
                        val apellidos = doc.getString("apellidos") ?: ""
                        val correo = doc.getString("email") ?: auth.currentUser?.email ?: ""
                        if (nombre.isNotEmpty()) txtNombre.text = "Nombre: $nombre"
                        if (apellidos.isNotEmpty()) txtApellidos.text = "Apellidos: $apellidos"
                        txtCorreo.text = "Correo: $correo"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "No se pudo cargar perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
