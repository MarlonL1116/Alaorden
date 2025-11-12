package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Recibe la data class 'Producto'
class DetallePedidoAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<DetallePedidoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detalleproducto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = productos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvCantidad.text = "${producto.cantidad}x"
        holder.tvNombre.text = producto.nombre
        // Muestra el subtotal (precio * cantidad)
        val subtotal = producto.precio * producto.cantidad
        holder.tvPrecio.text = "S/ %.2f".format(subtotal)
    }
}