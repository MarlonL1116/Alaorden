package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class CarritoAdapter(
    private var items: MutableList<Producto>,
    private val onCarritoChanged: (List<Producto>, Double) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreCarrito)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidadCarrito)
        val txtSubtotal: TextView = itemView.findViewById(R.id.txtSubtotalCarrito) // 👈 asegúrate que existe en item_carrito.xml
        val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinusCarrito)
        val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlusCarrito)
        //val btnPagar: Button = itemView.findViewById(R.id.btnPagar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = items[position]
        val precioUnitario = producto.precio ?: 0.0
        val cantidad = producto.cantidad

        holder.txtNombre.text = producto.nombre ?: "Producto"
        holder.txtCantidad.text = "x$cantidad"

        // ✅ Solo mostramos el subtotal (cantidad * precio unitario)
        val subtotal = precioUnitario * cantidad
        holder.txtSubtotal.text = "S/. ${"%.2f".format(subtotal)}"

        holder.btnPlus.setOnClickListener {
            updateProducto(producto, agregar = true)
        }

        holder.btnMinus.setOnClickListener {
            updateProducto(producto, agregar = false)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateProducto(producto: Producto, agregar: Boolean) {
        scope.launch(Dispatchers.IO) {
            if (agregar) {
                CarritoManager.agregarProducto(producto)
            } else {
                CarritoManager.quitarProducto(producto)
            }

            val nuevaLista = CarritoManager.obtenerCarrito().filter { it.cantidad > 0 }
            val total = nuevaLista.sumOf { (it.precio ?: 0.0) * it.cantidad }

            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(nuevaLista)
                notifyDataSetChanged()

                // ✅ Enviamos total actualizado a CarritoActivity
                onCarritoChanged(items, total)
            }
        }
    }

    fun updateList(newList: List<Producto>) {
        items.clear()
        items.addAll(newList.filter { it.cantidad > 0 })
        notifyDataSetChanged()

        val total = items.sumOf { (it.precio ?: 0.0) * it.cantidad }
        onCarritoChanged(items, total)
    }

    fun clear() {
        scope.cancel()
    }
}
