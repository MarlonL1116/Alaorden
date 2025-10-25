package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(
    private val items: List<Address>,
    private val onEdit: (Address) -> Unit,
    private val onDelete: (Address) -> Unit,
    private val onSelect: ((Address) -> Unit)? = null
) : RecyclerView.Adapter<AddressAdapter.VH>() {

    class VH(item: View) : RecyclerView.ViewHolder(item) {
        val tvTitle: TextView = item.findViewById(R.id.tvAddressTitle)
        val tvStreet: TextView = item.findViewById(R.id.tvStreet)
        val tvNotes: TextView = item.findViewById(R.id.tvNotes)
        val btnEdit: ImageView = item.findViewById(R.id.btnEditAddress)
        val btnDelete: ImageView = item.findViewById(R.id.btnDeleteAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]
        holder.tvTitle.text = a.title
        holder.tvStreet.text = a.street
        holder.tvNotes.text = a.notes ?: ""

        holder.btnEdit.setOnClickListener { onEdit(a) }
        holder.btnDelete.setOnClickListener { onDelete(a) }

        holder.itemView.setOnClickListener { onSelect?.invoke(a) }
    }

    override fun getItemCount(): Int = items.size
}
