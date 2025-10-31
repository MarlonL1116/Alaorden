package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddressesActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val addresses = mutableListOf<Address>()
    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresses)


        val selectMode = intent.getBooleanExtra("SELECT_MODE", false)
        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerAddresses)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = AddressAdapter(
            addresses,
            onEdit = { address ->
                Toast.makeText(this, "Editar: ${address.title}", Toast.LENGTH_SHORT).show()
                val i = Intent(this, EditAddressActivity::class.java)
                i.putExtra("ADDRESS_ID", address.id)
                startActivity(i)
            },
            onDelete = { address ->
                Toast.makeText(this, "Eliminar: ${address.title}", Toast.LENGTH_SHORT).show()
                deleteAddress(address)
            },
            onSelect = if (selectMode) { address ->
                saveSelectedAddress(address)
            } else null
        )

        recycler.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddAddress).setOnClickListener {
            startActivity(Intent(this, EditAddressActivity::class.java))
        }

        loadAddresses()
    }

    override fun onResume() {
        super.onResume()
        loadAddresses()
    }

    private fun loadAddresses() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("addresses")
            .get()
            .addOnSuccessListener { result ->
                addresses.clear()
                for (doc in result) {
                    val a = doc.toObject(Address::class.java)
                    a.id = doc.id
                    addresses.add(a)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAddress(address: Address) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("addresses").document(address.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Dirección eliminada", Toast.LENGTH_SHORT).show()
                loadAddresses()
            }
    }

    private fun saveSelectedAddress(address: Address) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("selectedAddress", mapOf(
                "title" to address.title,
                "street" to address.street,
                "latitude" to address.latitude,
                "longitude" to address.longitude
            ))
            .addOnSuccessListener {
                Toast.makeText(this, "Dirección seleccionada: ${address.title}", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la dirección", Toast.LENGTH_SHORT).show()
            }
    }
}
