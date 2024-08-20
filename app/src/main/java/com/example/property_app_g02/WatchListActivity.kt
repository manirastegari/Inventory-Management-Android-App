package com.example.property_app_g02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.property_app_g02.databinding.ActivityWatchListBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WatchListActivity : AppCompatActivity(), OnRemoveClickListener {
    private lateinit var binding: ActivityWatchListBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            val snackbar = Snackbar.make(binding.root, "Please log in to view your watchlist", Snackbar.LENGTH_LONG)
            snackbar.show()
            finish()
            return
        }

        if (supportActionBar != null) {
            supportActionBar!!.setTitle("Watch List")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        loadWatchlist()
    }

    private fun loadWatchlist() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("watchlist")
            .get()
            .addOnSuccessListener { documents ->
                val listings = documents.map { it.toObject(Listing::class.java) }
                binding.recyclerView.adapter = WatchListAdapter(listings, this)
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error: ", e)
            }
    }

    override fun onRemoveClick(listing: Listing) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("watchlist")
            .document(listing.id)
            .delete()
            .addOnSuccessListener {
                loadWatchlist()
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
            android.R.id.home -> {
                this.finish()
                true
            }
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