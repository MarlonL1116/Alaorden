package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ConfirmacionPagoActivity : AppCompatActivity() {

    private lateinit var tvMensajeConfirmacion: TextView
    private lateinit var tvNumeroPedido: TextView
    private lateinit var tvFechaPedido: TextView
    private lateinit var tvTiempoEntrega: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvCostoEntrega: TextView
    private lateinit var tvTotalPago: TextView
    private lateinit var btnVolverInicio: Button

    private var totalCarrito: Double = 0.0
    private var metodoPago: String = "Desconocido"

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion_pago)

        // Referencias UI
        tvMensajeConfirmacion = findViewById(R.id.tvMensajeConfirmacion)
        tvNumeroPedido = findViewById(R.id.tvNumeroPedido)
        tvFechaPedido = findViewById(R.id.tvFechaPedido)
        tvTiempoEntrega = findViewById(R.id.tvTiempoEntrega)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvCostoEntrega = findViewById(R.id.tvCostoEntrega)
        tvTotalPago = findViewById(R.id.tvTotalPago)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)

        totalCarrito = intent.getDoubleExtra("TOTAL_CARRITO", 0.0)
        metodoPago = intent.getStringExtra("METODO_PAGO") ?: "Desconocido"

        // Mostrar resumen
        tvTotalPago.text = "S/. ${"%.2f".format(totalCarrito)}"
        tvSubtotal.text = "S/. ${"%.2f".format(totalCarrito - 5)}"
        tvCostoEntrega.text = "S/. 5.00"
        tvTiempoEntrega.text = "30 - 40 min"

        crearPedidoYGuardarEnHistorial()

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun crearPedidoYGuardarEnHistorial() {
        val uid = auth.currentUser?.uid ?: return
        val carrito = CarritoManager.obtenerCarrito()

        if (carrito.isEmpty()) {
            Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { docUser ->
                val direccion = docUser.get("selectedAddress") as? Map<*, *>
                if (direccion == null) {
                    Toast.makeText(this, "Selecciona una dirección antes de confirmar.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val primerProducto = carrito.firstOrNull()
                val productoNombre = primerProducto?.nombre ?: "Producto sin nombre"
                val establecimientoId = primerProducto?.idEstablecimiento ?: "sin_id"

                // ✅ Crear objeto Order (ticket actual)
                val order = Order(
                    id = "",
                    userId = uid,
                    establecimientoId = establecimientoId,
                    establecimientoName = productoNombre,
                    direccion = OrderAddress(
                        title = direccion["title"].toString(),
                        street = direccion["street"].toString(),
                        notes = direccion["notes"]?.toString(),
                        latitude = (direccion["latitude"] as? Double) ?: 0.0,
                        longitude = (direccion["longitude"] as? Double) ?: 0.0
                    ),
                    productos = carrito.mapIndexed { index, it ->
                        OrderProduct(
                            id = it.id ?: "prod_$index",
                            nombre = it.nombre,
                            precio = it.precio,
                            cantidad = it.cantidad
                        )
                    },
                    total = totalCarrito,
                    status = "received",
                    createdAt = Timestamp.now()
                )

                val orderRef = db.collection("users")
                    .document(uid)
                    .collection("orders")
                    .document()

                order.id = orderRef.id

                // 🔹 Guardar pedido activo (ticket)
                orderRef.set(order)
                    .addOnSuccessListener {
                        // 🔹 Crear copia en historial con modelo Pedido
                        val pedido = Pedido(
                            id = order.id,
                            idUsuario = order.userId,
                            establecimientoId = order.establecimientoId,
                            nombreEstablecimiento = order.establecimientoName,
                            total = order.total,
                            productos = carrito,
                            fecha = order.createdAt
                        )

                        db.collection("historial")
                            .document(uid)
                            .collection("pedidos")
                            .document(order.id)
                            .set(pedido)
                            .addOnSuccessListener {
                                CarritoManager.vaciarCarrito()

                                tvMensajeConfirmacion.text = "PAGO EXITOSO CON $metodoPago"
                                tvNumeroPedido.text = "#${order.id.take(6)}"
                                tvFechaPedido.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                Toast.makeText(this, "Pedido agregado al historial ✅", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar en historial: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al crear pedido: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
    }

}
