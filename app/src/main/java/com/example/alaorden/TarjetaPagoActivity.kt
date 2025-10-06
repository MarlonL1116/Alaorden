package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.alaorden.ui.theme.ALaOrdenTheme

class TarjetaPagoActivity : AppCompatActivity() {
    private lateinit var etNumeroTarjeta: EditText
    private lateinit var etFechaVencimiento: EditText
    private lateinit var etCvv: EditText
    private lateinit var btnConfirmarPago: Button
    private var totalCarrito: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_pago)

        etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta)
        etFechaVencimiento = findViewById(R.id.etFechaVencimiento)
        etCvv = findViewById(R.id.etCvv)
        btnConfirmarPago = findViewById(R.id.btnConfirmarPago)

        // Obtener el total enviado
        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)

        btnConfirmarPago.setOnClickListener {
            val numero = etNumeroTarjeta.text.toString()
            val fecha = etFechaVencimiento.text.toString()
            val cvv = etCvv.text.toString()

            if (numero.isBlank() || fecha.isBlank() || cvv.isBlank()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulación: pasamos al activity de confirmación
            val intent = Intent(this, ConfirmacionPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            startActivity(intent)
            finish()
        }
    }
}