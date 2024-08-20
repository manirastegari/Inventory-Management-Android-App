package com.example.property_app_g02

import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.property_app_g02.databinding.ActivityListingsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListingsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var binding: ActivityListingsBinding
    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private val markerMap = mutableMapOf<Marker, Listing>()
    private var selectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSearch.setOnClickListener {
            val maxPrice = binding.etMaxPrice.text.toString().toDoubleOrNull()
            searchByMaxPrice(maxPrice)
        }

        binding.btnAddToWatchlist.setOnClickListener {
            val listing = selectedMarker?.let { markerMap[it] }
            if (listing != null) {
                if (auth.currentUser != null) {
                    addToWatchlist(listing)
                } else {
                    val snackbar = Snackbar.make(binding.root, "Please log in to add to watchlist", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MAP", "onMapReady is executing...")
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.setOnMarkerClickListener(this)

        val initialLocation = LatLng(43.6426, -79.3871)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10.0f))

        loadListings()
    }

    private fun loadListings() {
        db.collection("listings")
            .whereEqualTo("available", true)
            .get()
            .addOnSuccessListener { documents ->
                mMap.clear()
                markerMap.clear()
                var firstMarker: Marker? = null
                for (document in documents) {
                    val listing = document.toObject(Listing::class.java)
                    val location = LatLng(listing.lat, listing.lng)
                    val marker = mMap.addMarker(MarkerOptions().position(location).title("${listing.price} / month"))
                    if (marker != null) {
                        markerMap[marker] = listing
                        if (firstMarker == null) {
                            firstMarker = marker
                        }
                    }
                }
                firstMarker?.let {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 12.0f))
                }
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    private fun searchByMaxPrice(maxPrice: Double?) {
        if (maxPrice == null || maxPrice <= 0) {
            loadListings()
            return
        }

        db.collection("listings")
            .whereLessThanOrEqualTo("price", maxPrice)
            .whereEqualTo("available", true)
            .get()
            .addOnSuccessListener { documents ->
                mMap.clear()
                markerMap.clear()
                var firstMarker: Marker? = null
                for (document in documents) {
                    val listing = document.toObject(Listing::class.java)
                    val location = LatLng(listing.lat, listing.lng)
                    val marker = mMap.addMarker(MarkerOptions().position(location).title("${listing.price} / month"))
                    if (marker != null) {
                        markerMap[marker] = listing
                        if (firstMarker == null) {
                            firstMarker = marker
                        }
                    }
                }
                firstMarker?.let {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 12.0f))
                }
            }
            .addOnFailureListener { e ->
                Log.e("SearchByMaxPrice", "Error: ", e)
                val snackbar = Snackbar.make(binding.root, "Error: ${e}", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        val listing = markerMap[marker]
        if (listing != null) {
            binding.propertyDetails.visibility = View.VISIBLE
            binding.propertyPrice.text = "Price: ${listing.price}"
            binding.propertyAddress.text = "Address: ${listing.address}"
            binding.propertyBedrooms.text = "Bedrooms: ${listing.bedrooms}"
            Glide.with(this).load(listing.imageUrl).into(binding.propertyImage)
            selectedMarker = marker
        }
        return true
    }

    private fun addToWatchlist(listing: Listing) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("watchlist").add(listing)
            .addOnSuccessListener {
                val snackbar = Snackbar.make(binding.root, "Added to Watchlist", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options_listings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            R.id.logout -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            R.id.my_watch_list -> {
                if (auth.currentUser != null) {
                    startActivity(Intent(this, WatchListActivity::class.java))
                } else {
                    val snackbar = Snackbar.make(binding.root, "Please log in to view your watchlist", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}