package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderEstablecimiento: TextView = view.findViewById(R.id.tvOrderEstablecimiento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Pedido #${order.id.takeLast(5)}"
        holder.tvOrderTotal.text = "Total: S/ %.2f".format(order.total)
        holder.tvOrderEstablecimiento.text = order.establecimientoName

        val statusText = when (order.status) {
            "received" -> "Recibido"
            "in_transit" -> "En camino"
            "delivered" -> "Entregado"
            else -> order.status
        }
        holder.tvOrderStatus.text = "Estado: $statusText"

        // Colores según estado
        val color = when (order.status) {
            "received" -> android.R.color.holo_blue_light
            "in_transit" -> android.R.color.holo_orange_light
            "delivered" -> android.R.color.holo_green_dark
            else -> android.R.color.darker_gray
        }
        holder.tvOrderStatus.setTextColor(holder.itemView.resources.getColor(color))
    }

    override fun getItemCount(): Int = orders.size
}
