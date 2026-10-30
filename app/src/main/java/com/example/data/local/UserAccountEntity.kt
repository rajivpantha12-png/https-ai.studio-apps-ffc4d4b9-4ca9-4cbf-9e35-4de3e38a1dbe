package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey
    val id: String, // e.g. "USR-001" or email
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String, // "PASSENGER", "DRIVER", "FLEET_OWNER"
    val passengerId: String? = null, // e.g. "PAX-8492"
    val officeId: String? = null, // e.g. "OFC-TRANSIT-01"
    val drivingLicenseNumber: String? = null, // e.g. "DL-99482-TX"
    val isLicenseVerified: Boolean = false,
    val licenseExpiryDate: String? = null, // e.g. "2028-11-15"
    val assignedVehicleNumber: String? = null, // e.g. "VAN-04"
    val ratingScore: Double = 4.8,
    val ratingsCount: Int = 12,
    val createdAt: Long = System.currentTimeMillis()
)
