package com.example.alaorden

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductosAdapter(
    private var productos: MutableList<Producto>,
    private val onCarritoChanged: (List<Producto>, Double) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val descripcion: TextView = itemView.findViewById(R.id.txtDescripcionProducto)
        val precio: TextView = itemView.findViewById(R.id.txtPrecioProducto)
        val imagen: ImageView = itemView.findViewById(R.id.imgProducto)
        val btnAgregar: Button = itemView.findViewById(R.id.btnAgregar)
        val btnQuitar: Button = itemView.findViewById(R.id.btnQuitar)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad) // Stock disponible
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.nombre.text = producto.nombre
        holder.descripcion.text = producto.descripcion
        holder.precio.text = "S/. ${producto.precio}"
        holder.txtCantidad.text = producto.cantidad.toString()

        // ➕ Botón Agregar
        holder.btnAgregar.setOnClickListener {
            if (producto.cantidad > 0) {
                producto.cantidad -= 1
                CarritoManager.agregarProducto(producto.copy(cantidad = 1)) // solo 1 por click
                holder.txtCantidad.text = producto.cantidad.toString()
                notificarCambio()

                // 🔥 Actualizar Firestore para disminuir stock
                val db = FirebaseFirestore.getInstance()
                db.collection("establecimientos")
                    .document(producto.idEstablecimiento ?: "")
                    .collection("productos")
                    .document(producto.id ?: "")
                    .update("cantidad", producto.cantidad)
                    .addOnFailureListener {
                        Toast.makeText(holder.itemView.context, "Error al actualizar stock", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "Sin stock disponible", Toast.LENGTH_SHORT).show()
            }
        }

        // ➖ Botón Quitar
        holder.btnQuitar.setOnClickListener {
            CarritoManager.quitarProducto(producto.copy(cantidad = 1))

            producto.cantidad += 1
            holder.txtCantidad.text = producto.cantidad.toString()
            notificarCambio()

            // 🔥 Actualizar Firestore para devolver stock
            val db = FirebaseFirestore.getInstance()
            db.collection("establecimientos")
                .document(producto.idEstablecimiento ?: "")
                .collection("productos")
                .document(producto.id ?: "")
                .update("cantidad", producto.cantidad)
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Error al actualizar stock", Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun getItemCount(): Int = productos.size

    private fun notificarCambio() {
        val carrito = CarritoManager.obtenerCarrito()
        val total = carrito.sumOf { (it.precio ?: 0.0) * it.cantidad }
        onCarritoChanged(carrito, total)
    }

    fun updateList(newList: List<Producto>) {
        productos.clear()
        productos.addAll(newList)
        notifyDataSetChanged()
    }
}
