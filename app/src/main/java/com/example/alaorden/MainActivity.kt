package com.example.alaorden

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los botones
        val btnRestaurants = findViewById<Button>(R.id.btnRestaurants)
        val btnStores = findViewById<Button>(R.id.btnStores)

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
