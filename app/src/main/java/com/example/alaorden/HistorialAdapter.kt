package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(private val lista: List<Pedido>) :
    RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtEstablecimiento: TextView = itemView.findViewById(R.id.txtEstablecimiento)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotalHistorial)
        val txtProductos: TextView = itemView.findViewById(R.id.txtProductos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_historial, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedido = lista[position]
        holder.txtEstablecimiento.text = pedido.nombreEstablecimiento
        holder.txtTotal.text = "Total: S/. ${"%.2f".format(pedido.total)}"
        holder.txtProductos.text = pedido.productos.joinToString("\n") {
            "- ${it.nombre} (x${it.cantidad})"
        }
    }

    override fun getItemCount(): Int = lista.size
}
