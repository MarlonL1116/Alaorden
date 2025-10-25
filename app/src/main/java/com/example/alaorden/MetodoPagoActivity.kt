package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MetodoPagoActivity : AppCompatActivity() {

    private lateinit var totalText: TextView
    private lateinit var cardYape: CardView
    private lateinit var cardPlin: CardView
    private lateinit var cardTarjeta: CardView
    private var totalCarrito: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metodopago)

        // Referencias UI
        totalText = findViewById(R.id.tvTotal)
        cardTarjeta = findViewById(R.id.cardTarjeta)
        cardYape = findViewById(R.id.cardYape)
        cardPlin = findViewById(R.id.cardPlin)

        // Total a pagar
        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)
        totalText.text = "Total a pagar: S/. ${"%.2f".format(totalCarrito)}"

        // 💳 Botón TARJETA → ir a TarjetaPagoActivity
        cardTarjeta.setOnClickListener {
            val intent = Intent(this, TarjetaPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            startActivity(intent)
        }

        // 📱 Botón YAPE → ir a YapePlinActivity
        cardYape.setOnClickListener {
            val intent = Intent(this, YapePlinActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            intent.putExtra("METODO", "Yape")
            startActivity(intent)
        }

        // 📲 Botón PLIN → ir a YapePlinActivity
        cardPlin.setOnClickListener {
            val intent = Intent(this, YapePlinActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            intent.putExtra("METODO", "Plin")
            startActivity(intent)
        }
    }
}
