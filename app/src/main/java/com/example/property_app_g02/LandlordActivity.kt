package com.example.property_app_g02

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.property_app_g02.databinding.ActivityLandlordBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class LandlordActivity : AppCompatActivity() {
    lateinit var binding: ActivityLandlordBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_LIST = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandlordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            finish()
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnSubmitListing.setOnClickListener {
            val imageUrl = binding.etImageUrl.text.toString()
            val price = binding.etPrice.text.toString().toDouble()
            val bedrooms = binding.etBedrooms.text.toString().toInt()
            val latitude = binding.etLatitude.text.toString().toDouble()
            val longitude = binding.etLongitude.text.toString().toDouble()
            val available = binding.switchAvailable.isChecked
            val address = binding.etAddress.text.toString()

            val listing = Listing(
                landlordId = auth.currentUser!!.uid,
                imageUrl = imageUrl,
                price = price,
                bedrooms = bedrooms,
                available = available,
                lat = latitude,
                lng = longitude,
                address = address
            )

            val data = mapOf(
                "landlordId" to listing.landlordId,
                "imageUrl" to listing.imageUrl,
                "price" to listing.price,
                "bedrooms" to listing.bedrooms,
                "available" to listing.available,
                "lat" to latitude,
                "lng" to longitude,
                "address" to address
            )

            db.collection("listings").add(data)
                .addOnSuccessListener {
                    val snackbar = Snackbar.make(binding.root, "Property Added Successfully.", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                    clearInputs()
                }
                .addOnFailureListener { e ->
                    Log.e("ERROR", "Error adding document", e)
                }
        }

        val requestLocationPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsList ->
            var allPermissionsGranted = true
            for (entry in resultsList.entries) {
                if (entry.key in LOCATION_PERMISSION_LIST && !entry.value) {
                    allPermissionsGranted = false
                }
            }

            if (allPermissionsGranted) {
                binding.textView.text = "Getting your location..."
                findMyLocation()
            } else {
                binding.textView.text = "ERROR: You need to grant ALL permissions!"
            }
        }

        binding.btnUseCurrentLocation.setOnClickListener {
            requestLocationPermissionsLauncher.launch(LOCATION_PERMISSION_LIST)
        }

        binding.btnGetLatLng.setOnClickListener {
            val address = binding.etAddress.text.toString()
            if (address.isNotEmpty()) {
                getLatLngFromAddress(address)
            } else {
                val snackbar = Snackbar.make(binding.root, "Please enter an address.", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }

        binding.btnGetAddress.setOnClickListener {
            val latitude = binding.etLatitude.text.toString().toDoubleOrNull()
            val longitude = binding.etLongitude.text.toString().toDoubleOrNull()
            if (latitude != null && longitude != null) {
                getAddressFromLatLng(latitude, longitude)
            } else {
                val snackbar = Snackbar.make(binding.root, "Please enter valid latitude and longitude.", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options_landlord, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.landlordActivity -> {
                true
            }
            R.id.LandlordListingActivity -> {
                startActivity(Intent(this, LandlordListingActivity::class.java))
                true
            }
            R.id.logout -> {
                auth.signOut()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission")
    fun findMyLocation() {
        val cancellationTokenSource = CancellationTokenSource()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                binding.etLatitude.setText(location.latitude.toString())
                binding.etLongitude.setText(location.longitude.toString())
                binding.textView.text = "Location: ${location.latitude}, ${location.longitude}"

                val geocoder = Geocoder(this, Locale.getDefault())
                val searchResults: MutableList<android.location.Address>? =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!searchResults.isNullOrEmpty()) {
                    val address = searchResults[0].getAddressLine(0)
                    binding.etAddress.setText(address)
                }
            }
            .addOnFailureListener { e ->
                binding.textView.text = "Error: $e"
            }
    }

    private fun getLatLngFromAddress(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val searchResults: MutableList<android.location.Address>? =
            geocoder.getFromLocationName(address, 1)
        if (!searchResults.isNullOrEmpty()) {
            val location = searchResults[0]
            binding.etLatitude.setText(location.latitude.toString())
            binding.etLongitude.setText(location.longitude.toString())
        } else {
            val snackbar = Snackbar.make(binding.root, "Address not found.", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val searchResults: MutableList<android.location.Address>? =
            geocoder.getFromLocation(latitude, longitude, 1)
        if (!searchResults.isNullOrEmpty()) {
            val address = searchResults[0].getAddressLine(0)
            binding.etAddress.setText(address)
        } else {
            val snackbar = Snackbar.make(binding.root, "Address not found.", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }

    private fun clearInputs() {
        binding.etImageUrl.setText("")
        binding.etPrice.setText("")
        binding.etBedrooms.setText("")
        binding.etLatitude.setText("")
        binding.etLongitude.setText("")
        binding.switchAvailable.isChecked = true
        binding.etAddress.setText("")
    }
}