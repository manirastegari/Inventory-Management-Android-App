package com.example.property_app_g02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.property_app_g02.databinding.ActivityLandlordListingBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LandlordListingActivity : AppCompatActivity(), ClickDetectorInterface {
    private lateinit var binding: ActivityLandlordListingBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ListingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandlordListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            finish()
            return
        }

        setupRecyclerView()
        loadListings()
    }

    private fun setupRecyclerView() {
        adapter = ListingAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun loadListings() {
        val userId = auth.currentUser!!.uid
        db.collection("listings")
            .whereEqualTo("landlordId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val listings = documents.toObjects(Listing::class.java)
                adapter.submitList(listings)
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    override fun onResume() {
        super.onResume()
        loadListings()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options_landlord, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.landlordActivity -> {
                startActivity(Intent(this, LandlordActivity::class.java))
                true
            }
            R.id.LandlordListingActivity -> {
                true
            }
            R.id.logout -> {
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDeleteClick(listing: Listing) {
        db.collection("listings").document(listing.id)
            .delete()
            .addOnSuccessListener {
                val snackbar = Snackbar.make(binding.root, "Property Deleted Successfully.", Snackbar.LENGTH_SHORT)
                snackbar.show()
                loadListings()
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    override fun onEditClick(listing: Listing) {
        val intent = Intent(this, UpdateListingActivity::class.java)
        intent.putExtra("listingId", listing.id)
        startActivity(intent)
    }
}