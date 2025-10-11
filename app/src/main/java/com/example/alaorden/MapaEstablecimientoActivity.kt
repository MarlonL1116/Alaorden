package com.example.alaorden

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaEstablecimientoActivity : AppCompatActivity(), OnMapReadyCallback {

    private var lat = 0.0
    private var lng = 0.0
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_establecimiento)

        name = intent.getStringExtra("EST_NAME") ?: ""
        lat = intent.getDoubleExtra("EST_LAT", 0.0)
        lng = intent.getDoubleExtra("EST_LNG", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.d("MapaEstablecimiento", "Lat: $lat, Lng: $lng, Name: $name")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MapaEstablecimiento", "Mapa cargado. Lat=$lat, Lng=$lng, Name=$name")

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true

        val pos = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(pos).title(name))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
    }

}
