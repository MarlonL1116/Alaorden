package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductosAdapter(
    private var productos: MutableList<Producto>,
    private val onCarritoChanged: (List<Producto>, Double) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val descripcion: TextView = itemView.findViewById(R.id.txtDescripcionProducto)
        val precio: TextView = itemView.findViewById(R.id.txtPrecioProducto)
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val btnAgregar: ImageButton = itemView.findViewById(R.id.btnAgregar)
        val btnQuitar: ImageButton = itemView.findViewById(R.id.btnQuitar)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        // 🔹 Inicializar siempre en 0 (sin mostrar stock real)
        if (producto.cantidad < 0) producto.cantidad = 0

        holder.nombre.text = producto.nombre ?: ""
        holder.descripcion.text = producto.descripcion ?: ""
        holder.precio.text = "S/. ${"%.2f".format(producto.precio)}"
        holder.txtCantidad.text = producto.cantidad.toString()

        // Imagen del producto
        Glide.with(holder.itemView.context)
            .load(producto.imageUrl)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imgProducto)

        // ➕ Botón Agregar
        holder.btnAgregar.setOnClickListener {
            producto.cantidad += 1
            holder.txtCantidad.text = producto.cantidad.toString()

            // Actualizar carrito
            CarritoManager.agregarProducto(producto)
            val carrito = CarritoManager.obtenerCarrito()
            val total = CarritoManager.obtenerTotal()
            onCarritoChanged(carrito, total)
        }

        // ➖ Botón Quitar
        holder.btnQuitar.setOnClickListener {
            if (producto.cantidad > 0) {
                producto.cantidad -= 1
                holder.txtCantidad.text = producto.cantidad.toString()

                CarritoManager.quitarProducto(producto)
                val carrito = CarritoManager.obtenerCarrito()
                val total = CarritoManager.obtenerTotal()
                onCarritoChanged(carrito, total)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "No puedes tener menos de 0",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int = productos.size

    fun updateList(newList: List<Producto>) {
        // 🔹 Siempre inicializar en 0 cuando se cargan productos
        productos.clear()
        newList.forEach { it.cantidad = 0 }
        productos.addAll(newList)
        notifyDataSetChanged()
    }
}
