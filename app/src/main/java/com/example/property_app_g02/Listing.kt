package com.example.property_app_g02

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Listing(
    @DocumentId
    val id: String = "",
    val landlordId: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val bedrooms: Int = 0,
    val available: Boolean = true,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = ""
)
