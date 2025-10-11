package com.example.alaorden

import android.content.Intent
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
        val btnVerMapa: ImageView = itemView.findViewById(R.id.btnVerMapa)
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

        Glide.with(holder.itemView.context)
            .load(est.imageUrl)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imgEstablecimiento)

        // click en todo el item -> abrir productos (ya lo tenías)
        holder.itemView.setOnClickListener {
            onItemClick(est)
        }

        // click en el botón de mapa -> abrir actividad de mapa
        holder.btnVerMapa.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, MapaEstablecimientoActivity::class.java).apply {
                putExtra("EST_NAME", est.name)
                // si son null, se mandan 0.0 (puedes cambiar comportamiento)
                putExtra("EST_LAT", est.latitude ?: 0.0)
                putExtra("EST_LNG", est.longitude ?: 0.0)
            }
            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun updateList(nuevaLista: List<Establecimientos>) {
        lista = nuevaLista
        listaOriginal = ArrayList(nuevaLista)
        notifyDataSetChanged()
    }

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
