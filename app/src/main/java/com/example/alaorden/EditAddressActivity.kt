package com.example.alaorden

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditAddressActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etStreet: EditText
    private lateinit var etNotes: EditText
    private lateinit var etLat: EditText
    private lateinit var etLng: EditText
    private lateinit var btnGetLocation: Button
    private lateinit var btnSave: Button

    private lateinit var fused: FusedLocationProviderClient
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var editingAddressId: String? = null

    companion object {
        private const val REQ_LOCATION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_address)
        // Encuentra tu layout de encabezado
        val headerLayout = findViewById<RelativeLayout>(R.id.header_carrito) // Usa el ID de tu header
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBarInsets.top)
            WindowInsetsCompat.CONSUMED
        }
        etTitle = findViewById(R.id.etTitle)
        etStreet = findViewById(R.id.etStreet)
        etNotes = findViewById(R.id.etNotes)
        etLat = findViewById(R.id.etLat)
        etLng = findViewById(R.id.etLng)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnSave = findViewById(R.id.btnSaveAddress)

        fused = LocationServices.getFusedLocationProviderClient(this)

        editingAddressId = intent.getStringExtra("ADDRESS_ID")
        editingAddressId?.let { loadAddressForEdit(it) }

        btnGetLocation.setOnClickListener { getLocationIfAllowed() }
        btnSave.setOnClickListener { saveAddress() }
    }

    /** Carga la dirección actual para editarla */
    private fun loadAddressForEdit(id: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("addresses").document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val a = doc.toObject(Address::class.java)
                    a?.let {
                        etTitle.setText(it.title)
                        etStreet.setText(it.street)
                        etNotes.setText(it.notes)
                        etLat.setText((it.latitude ?: 0.0).toString())
                        etLng.setText((it.longitude ?: 0.0).toString())
                    }
                }
            }
    }

    /** Obtiene ubicación actual si el permiso está concedido */
    private fun getLocationIfAllowed() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_LOCATION)
            return
        }

        fused.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                etLat.setText(location.latitude.toString())
                etLng.setText(location.longitude.toString())
                Toast.makeText(this, "Ubicación obtenida ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Resultado del permiso */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationIfAllowed()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /** Guarda o actualiza la dirección */
    private fun saveAddress() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "No estás logueado", Toast.LENGTH_SHORT).show()
            return
        }

        val title = etTitle.text.toString().trim()
        val street = etStreet.text.toString().trim()
        val notes = etNotes.text.toString().trim()
        val lat = etLat.text.toString().toDoubleOrNull()
        val lng = etLng.text.toString().toDoubleOrNull()

        if (title.isEmpty() || street.isEmpty()) {
            Toast.makeText(this, "Completa título y dirección", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "title" to title,
            "street" to street,
            "notes" to notes,
            "latitude" to lat,
            "longitude" to lng,
            "createdAt" to Timestamp.now()
        )

        val ref = if (editingAddressId != null) {
            db.collection("users").document(uid).collection("addresses").document(editingAddressId!!)
        } else {
            db.collection("users").document(uid).collection("addresses").document()
        }

        ref.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Dirección guardada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
