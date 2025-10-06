package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        etCelular = findViewById(R.id.etCelular)
        etCodigo = findViewById(R.id.etCodigo)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        tvTitulo = findViewById(R.id.tvTitulo)

        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)
        metodo = intent.getStringExtra("METODO") ?: "Yape"
        tvTitulo.text = "Pago con $metodo"

        btnConfirmar.setOnClickListener {
            val celular = etCelular.text.toString()
            val codigo = etCodigo.text.toString()

            if (celular.length != 9 || codigo.length != 6) {
                Toast.makeText(this, "Verifique los datos ingresados", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ConfirmacionPagoActivity::class.java)
            intent.putExtra("TOTAL_CARRITO", totalCarrito)
            startActivity(intent)
            finish()
        }
    }
}