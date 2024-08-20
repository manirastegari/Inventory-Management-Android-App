package com.example.property_app_g02

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.property_app_g02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLandlord.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("USER_TYPE", "LANDLORD")
            startActivity(intent)
        }

        binding.btnRenter.setOnClickListener {
            val intent = Intent(this, ListingsActivity::class.java)
            startActivity(intent)
        }
    }
}