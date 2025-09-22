package com.example.alaorden

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MetodoPagoActivity : ComponentActivity() {
    private lateinit var totalText: TextView
    private var totalCarrito: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metodopago)

        totalText = findViewById(R.id.tvTotal)

        // Recuperar el total enviado desde CarritoActivity
        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)

        totalText.text = "Total a pagar: S/. ${"%.2f".format(totalCarrito)}"

        // Aquí podrías tener botones de métodos de pago (ejemplo: Yape, Plin, tarjeta)
    }
}