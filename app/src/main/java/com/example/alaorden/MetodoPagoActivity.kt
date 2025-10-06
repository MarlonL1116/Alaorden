package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class MetodoPagoActivity : AppCompatActivity() {
        private lateinit var totalText: TextView
        private var totalCarrito: Double = 0.0
        private lateinit var cardYape: CardView
         private lateinit var cardPlin: CardView
        private lateinit var cardTarjeta: CardView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_metodopago)

            totalText = findViewById(R.id.tvTotal)
            cardTarjeta = findViewById(R.id.cardTarjeta)
            cardYape = findViewById(R.id.cardYape)
            cardPlin = findViewById(R.id.cardPlin)

            totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)
            totalText.text = "Total a pagar: S/. ${"%.2f".format(totalCarrito)}"

            cardTarjeta.setOnClickListener {
                val intent = Intent(this, TarjetaPagoActivity::class.java)
                intent.putExtra("TOTAL_CARRITO", totalCarrito)
                startActivity(intent)
            }

            cardYape.setOnClickListener {
                val intent = Intent(this, YapePlinActivity::class.java)
                intent.putExtra("TOTAL_CARRITO", totalCarrito)
                intent.putExtra("METODO", "Yape")
                startActivity(intent)
            }

            cardPlin.setOnClickListener {
                val intent = Intent(this, YapePlinActivity::class.java)
                intent.putExtra("TOTAL_CARRITO", totalCarrito)
                intent.putExtra("METODO", "Plin")
                startActivity(intent)
            }
        }
    }