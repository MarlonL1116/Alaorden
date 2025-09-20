package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailInput = findViewById<EditText>(R.id.editTextEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnIngresar)
        val txtGoRegister = findViewById<TextView>(R.id.txtRegistrar)

        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                // Buscar el nombre en Firestore
                                db.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            val nombre = document.getString("name") ?: "Usuario"

                                            // Guardamos el nombre en SharedPreferences
                                            val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                                            with(sharedPref.edit()) {
                                                putString("name", nombre)
                                                apply()
                                            }

                                            Toast.makeText(this, "Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                                        }

                                        // Ir a MainActivity
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        txtGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()
        // Si ya está logueado, no volver a pedir login
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
