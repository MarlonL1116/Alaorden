package com.example.alaorden

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los botones
        val btnRestaurants = findViewById<CardView>(R.id.cardRestaurants)
        val btnStores = findViewById<CardView>(R.id.cardStore)

        // Acción al dar clic en Restaurants
        btnRestaurants.setOnClickListener {
            val intent = Intent(this, RestaurantsActivity::class.java)
            startActivity(intent)
        }

        // Acción al dar clic en Stores
        btnStores.setOnClickListener {
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)
        }
    }
}
