package com.example.alaorden

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class HistorialDetalleActivity : AppCompatActivity() {

    private lateinit var tvEstablecimiento: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvTotal: TextView
    private lateinit var recyclerProductos: RecyclerView

    private lateinit var productoAdapter: DetallePedidoAdapter
    private val productosList = mutableListOf<Producto>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_historial)

        // Ajustar padding de la barra de estado
        val headerLayout = findViewById<LinearLayout>(R.id.header_detalle_historial)
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBarInsets.top)
            WindowInsetsCompat.CONSUMED
        }

        // Referencias del Layout
        tvEstablecimiento = findViewById(R.id.tvDetalleEstablecimiento)
        tvFecha = findViewById(R.id.tvDetalleFecha)
        tvEstado = findViewById(R.id.tvDetalleEstado)
        tvTotal = findViewById(R.id.tvDetalleTotal)
        recyclerProductos = findViewById(R.id.recyclerDetalleProductos)

        // Configurar RecyclerView de productos
        recyclerProductos.layoutManager = LinearLayoutManager(this)
        productoAdapter = DetallePedidoAdapter(productosList)
        recyclerProductos.adapter = productoAdapter

        // Obtener el ID del pedido y cargar los datos
        val pedidoId = intent.getStringExtra("PEDIDO_ID")
        if (pedidoId == null) {
            Toast.makeText(this, "Error: No se encontró el pedido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDetallePedido(pedidoId)
    }

    private fun cargarDetallePedido(pedidoId: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("historial").document(uid)
            .collection("pedidos").document(pedidoId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val pedido = doc.toObject(Pedido::class.java) // Usa el modelo Pedido
                    if (pedido != null) {
                        // Llenar los datos en la UI
                        tvEstablecimiento.text = pedido.nombreEstablecimiento
                        tvTotal.text = "S/ %.2f".format(pedido.total)
                        tvEstado.text = "Estado: ${pedido.status.replaceFirstChar { it.uppercase() }}"

                        // Formatear la fecha
                        val timestamp = pedido.obtenerFechaTimestamp() // Usa la función del modelo
                        val sdf = SimpleDateFormat("dd 'de' MMMM yyyy, hh:mm a", Locale("es", "ES"))
                        tvFecha.text = "Fecha: ${sdf.format(timestamp.toDate())}"

                        // Llenar la lista de productos
                        productosList.clear()
                        productosList.addAll(pedido.productos)
                        productoAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this, "No se encontraron detalles", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar detalles", Toast.LENGTH_SHORT).show()
            }
    }
}