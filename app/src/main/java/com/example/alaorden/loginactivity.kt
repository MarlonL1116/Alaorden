package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Ir a MainActivity (pantalla principal) al ingresar
        btnIngresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // cerrar Login para que no se pueda volver atrás
        }

        // Ir a RegisterActivity al dar clic en "Registrarse"
        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
