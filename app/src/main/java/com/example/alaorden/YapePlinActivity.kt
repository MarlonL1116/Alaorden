package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class YapePlinActivity : AppCompatActivity() {

    private lateinit var etCelular: EditText
    private lateinit var etCodigo: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var tvTitulo: TextView
    private var totalCarrito: Double = 0.0
    private var metodo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yapeplin)

        // 🔹 Referencias
        etCelular = findViewById(R.id.etCelular)
        etCodigo = findViewById(R.id.etCodigo)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        tvTitulo = findViewById(R.id.tvTitulo)

        // 🔹 Obtener los datos del intent
        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)
        metodo = intent.getStringExtra("METODO") ?: "Yape"

        // 🔹 Mostrar título dinámico
        tvTitulo.text = "Pago con $metodo"

        // 🔹 Validar y continuar con la confirmación
        btnConfirmar.setOnClickListener {
            val celular = etCelular.text.toString()
            val codigo = etCodigo.text.toString()

            if (celular.length != 9 || codigo.length != 6) {
                Toast.makeText(this, "Verifique los datos ingresados", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Ahora pasamos también el método de pago
            val intent = Intent(this, ConfirmacionPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            intent.putExtra("METODO_PAGO", metodo)
            startActivity(intent)
            finish()
        }
    }
}
