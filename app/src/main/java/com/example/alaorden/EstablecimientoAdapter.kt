package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EstablecimientoAdapter(
    private var lista: List<Establecimientos>,
    private val onItemClick: (Establecimientos) -> Unit
) : RecyclerView.Adapter<EstablecimientoAdapter.ViewHolder>() {

    private var listaOriginal: List<Establecimientos> = lista

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtType: TextView = itemView.findViewById(R.id.txtType)
        val imgEstablecimiento: ImageView = itemView.findViewById(R.id.imgEstablecimiento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_establecimiento, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val est = lista[position]
        holder.txtName.text = est.name
        holder.txtType.text = est.type

        // 🔹 Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(est.imageUrl)
            .error(R.drawable.ic_launcher_background) // si falla carga un placeholder
            .into(holder.imgEstablecimiento)

        holder.itemView.setOnClickListener {
            onItemClick(est)
        }
    }

    override fun getItemCount(): Int = lista.size

    // 🔹 Actualiza lista desde Firebase
    fun updateList(nuevaLista: List<Establecimientos>) {
        lista = nuevaLista
        listaOriginal = ArrayList(nuevaLista) // guardamos copia para futuros filtros
        notifyDataSetChanged()
    }

    // 🔹 Filtra por nombre en tiempo real
    fun filter(query: String) {
        lista = if (query.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
