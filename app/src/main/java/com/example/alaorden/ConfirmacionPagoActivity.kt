package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

class ConfirmacionPagoActivity : AppCompatActivity() {
        private lateinit var tvMensajeConfirmacion: TextView
        private var totalCarrito: Double = 0.0
     private lateinit var btnVolverInicio: Button


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_confirmacion_pago)
            btnVolverInicio = findViewById(R.id.btnVolverInicio)
            tvMensajeConfirmacion = findViewById(R.id.tvMensajeConfirmacion)
            totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)


            CarritoManager.vaciarCarrito()

            tvMensajeConfirmacion.text = """
            Gracias por su compra.
            Su pedido llegará en aproximadamente 30 minutos.
            Monto pagado: S/. ${"%.2f".format(totalCarrito)}
            
        """.trimIndent()
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Cierra esta pantalla para evitar volver atrás
        }
        }
    }