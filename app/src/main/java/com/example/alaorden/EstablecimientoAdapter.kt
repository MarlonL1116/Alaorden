package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EstablecimientoAdapter(
    private var lista: List<Establecimientos>,
    private val onItemClick: (Establecimientos) -> Unit
) : RecyclerView.Adapter<EstablecimientoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtType: TextView = itemView.findViewById(R.id.txtType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_establecimiento, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val establecimiento = lista[position]
        holder.txtName.text = establecimiento.name
        holder.txtType.text = establecimiento.type

        holder.itemView.setOnClickListener {
            onItemClick(establecimiento)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun updateList(nuevaLista: List<Establecimientos>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}

