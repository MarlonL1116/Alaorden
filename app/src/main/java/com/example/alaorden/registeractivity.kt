package com.example.alaorden

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuthException
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeractivity)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias a los campos
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        // Acción al hacer clic en REGISTRARSE
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                // Crear usuario en Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid

                            // Crear objeto con datos adicionales
                            val userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "phone" to phone
                            )

                            // Guardar en Firestore
                            if (userId != null) {
                                db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                        finish() // Regresar al Login
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            val errorMessage = when ((task.exception as? FirebaseAuthException)?.errorCode) {
                                "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres."
                                "ERROR_INVALID_EMAIL" -> "El formato del correo electrónico es inválido."
                                "ERROR_EMAIL_ALREADY_IN_USE" -> "Ya existe una cuenta con este correo electrónico."
                                "ERROR_NETWORK_REQUEST_FAILED" -> "Problema de conexión a internet."
                                else -> "Error al registrar usuario: ${task.exception?.message}"
                            }
                            Log.e("RegisterActivity", "Error de registro: ${task.exception?.message}", task.exception)
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Enlace a Login
        txtLogin.setOnClickListener {
            finish() // Cierra registro y vuelve al login
        }
    }
}
