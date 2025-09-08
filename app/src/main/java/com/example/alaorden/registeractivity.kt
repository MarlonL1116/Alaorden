package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeractivity)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<TextView>(R.id.txtLogin)

        btnRegister.setOnClickListener {
            // De momento no guardamos nada
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}