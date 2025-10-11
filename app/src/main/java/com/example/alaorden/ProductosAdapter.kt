package com.example.alaorden

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val btnAgregar: Button = itemView.findViewById(R.id.btnAgregar)
        val btnQuitar: Button = itemView.findViewById(R.id.btnQuitar)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
    Log.d("ProductosAdapter", "Producto: $producto")
        holder.nombre.text = producto.nombre ?: ""
        holder.descripcion.text = producto.descripcion ?: ""
        holder.precio.text = "S/. ${"%.2f".format(producto.precio)}"
        holder.txtCantidad.text = producto.cantidad.toString()
        // 🔹 Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(producto.imageUrl)
            .error(R.drawable.ic_launcher_background) // si falla carga un placeholder
            .into(holder.imgProducto)

        holder.btnAgregar.setOnClickListener {
            if (producto.cantidad > 0) {
                // ⚡ Validamos si el carrito ya tiene productos de otro establecimiento
                if (CarritoManager.obtenerCarrito().isNotEmpty()) {
                    val idEstablecimientoCarrito = CarritoManager.obtenerCarrito()[0].idEstablecimiento
                    if (idEstablecimientoCarrito != producto.idEstablecimiento) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Solo puedes agregar productos del mismo establecimiento",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }

                producto.cantidad -= 1
                CarritoManager.agregarProducto(producto.copy(cantidad = 1))
                holder.txtCantidad.text = producto.cantidad.toString()

                val carrito = CarritoManager.obtenerCarrito()
                val total = CarritoManager.obtenerTotal()
                onCarritoChanged(carrito, total)

            } else {
                Toast.makeText(holder.itemView.context, "Sin stock disponible", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnQuitar.setOnClickListener {
            // ✅ Ahora quitarProducto devuelve Boolean
            val success = CarritoManager.quitarProducto(producto)
            if (success) {
                producto.cantidad += 1
                holder.txtCantidad.text = producto.cantidad.toString()
                val carrito = CarritoManager.obtenerCarrito()
                val total = CarritoManager.obtenerTotal()
                onCarritoChanged(carrito, total)
            } else {
                Toast.makeText(holder.itemView.context, "No hay en carrito para quitar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = productos.size

    fun updateList(newList: List<Producto>) {
        Log.d("ProductosAdapter", "Actualizando lista de productos: ${newList.size}")

        productos.addAll(newList)
        notifyDataSetChanged()
    }
}
