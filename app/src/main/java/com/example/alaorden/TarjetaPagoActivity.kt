package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

        // ✅ Recibir el total del carrito
        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)

        // ✅ Confirmar pago y pasar al Activity de confirmación
        btnConfirmarPago.setOnClickListener {
            val numero = etNumeroTarjeta.text.toString()
            val fecha = etFechaVencimiento.text.toString()
            val cvv = etCvv.text.toString()

            if (numero.isBlank() || fecha.isBlank() || cvv.isBlank()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Pasar datos al activity de confirmación
            val intent = Intent(this, ConfirmacionPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            intent.putExtra("METODO_PAGO", "Tarjeta")
            startActivity(intent)
            finish()
        }
    }
}
