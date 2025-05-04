package com.techmania.grocify.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Address")
data class Address(
    @PrimaryKey val id: Int = 1,  // Fixed ID since we're saving only one address
    val name: String?,
    val phoneNumber: String?,
    val label: String?,           // Optional label (e.g., "Home")
    val addressLine1: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val latitude: Double?,
    val longitude: Double?,

)
