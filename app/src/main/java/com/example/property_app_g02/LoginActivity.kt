
package com.example.property_app_g02

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.property_app_g02.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (supportActionBar != null) {
            supportActionBar!!.setTitle("Login")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userType = intent.getStringExtra("USER_TYPE")
                        if (userType == "LANDLORD") {
                            startActivity(Intent(this, LandlordActivity::class.java))
                        } else if (userType == "RENTER") {
                            startActivity(Intent(this, ListingsActivity::class.java))
                        }
                        finish()
                    } else {
                        var snackbar = Snackbar.make(binding.root, "Authentication failed.", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
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