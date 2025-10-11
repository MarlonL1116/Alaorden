package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConfirmacionPagoActivity : AppCompatActivity() {

    private lateinit var tvMensajeConfirmacion: TextView
    private lateinit var btnVolverInicio: Button
    private var totalCarrito: Double = 0.0

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion_pago)

        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        tvMensajeConfirmacion = findViewById(R.id.tvMensajeConfirmacion)
        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)

        // ✅ Guardar pedido antes de vaciar el carrito
        guardarPedidoEnHistorial()

        tvMensajeConfirmacion.text = """
            Gracias por su compra.
            Su pedido llegará en aproximadamente 30 minutos.
            Monto pagado: S/. ${"%.2f".format(totalCarrito)}
        """.trimIndent()

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun guardarPedidoEnHistorial() {
        val usuarioId = auth.currentUser?.uid ?: "anonimo"
        val productos = CarritoManager.obtenerCarrito()
        val establecimientoId = CarritoManager.obtenerEstablecimiento() ?: "desconocido"

        if (productos.isEmpty()) {
            Toast.makeText(this, "No hay productos para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtenemos el nombre del establecimiento
        db.collection("establecimientos").document(establecimientoId)
            .get()
            .addOnSuccessListener { doc ->
                val nombreEstablecimiento = doc.getString("name") ?: "Sin nombre"

                val pedido = Pedido(
                    idUsuario = usuarioId,
                    establecimientoId = establecimientoId,
                    nombreEstablecimiento = nombreEstablecimiento,
                    total = totalCarrito,
                    productos = productos
                )

                db.collection("historial")
                    .document(usuarioId)
                    .collection("pedidos")
                    .add(pedido)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Pedido guardado en historial", Toast.LENGTH_SHORT).show()
                        CarritoManager.vaciarCarrito() // ahora sí vaciamos el carrito
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar pedido", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
