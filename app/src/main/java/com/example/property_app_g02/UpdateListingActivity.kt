

package com.example.property_app_g02

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.property_app_g02.databinding.ActivityUpdateListingBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateListingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateListingBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listingId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listingId = intent.getStringExtra("listingId") ?: return

        if (supportActionBar != null) {
            supportActionBar!!.setTitle("Update Listing")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        loadListingDetails()

        binding.btnUpdate.setOnClickListener {
            updateListing()
        }
    }

    private fun loadListingDetails() {
        db.collection("listings").document(listingId)
            .get()
            .addOnSuccessListener { document ->
                val listing = document.toObject(Listing::class.java)
                if (listing != null) {
                    binding.etPrice.setText(listing.price.toString())
                    binding.etBedrooms.setText(listing.bedrooms.toString())
                    binding.etImageUrl.setText(listing.imageUrl)
                    binding.etLatitude.setText(listing.lat.toString())
                    binding.etLongitude.setText(listing.lng.toString())
                    binding.switchAvailable.isChecked = listing.available
                    binding.etAddress.setText(listing.address)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    private fun updateListing() {
        val price = binding.etPrice.text.toString().toDouble()
        val bedrooms = binding.etBedrooms.text.toString().toInt()
        val imageUrl = binding.etImageUrl.text.toString()
        val latitude = binding.etLatitude.text.toString().toDouble()
        val longitude = binding.etLongitude.text.toString().toDouble()
        val available = binding.switchAvailable.isChecked
        val address = binding.etAddress.text.toString()

        val data = mapOf(
            "price" to price,
            "bedrooms" to bedrooms,
            "imageUrl" to imageUrl,
            "available" to available,
            "lat" to latitude,
            "lng" to longitude,
            "address" to address
        )

        db.collection("listings").document(listingId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                val snackbar = Snackbar.make(binding.root, "Listing updated", Snackbar.LENGTH_SHORT)
                snackbar.show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
