package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver_ratings")
data class DriverRatingEntity(
    @PrimaryKey
    val ratingId: String,
    val driverId: String,
    val driverName: String,
    val passengerId: String,
    val passengerName: String,
    val stars: Int, // 1 to 5
    val punctualityScore: Int,
    val safetyScore: Int,
    val serviceScore: Int,
    val feedbackText: String,
    val monthYear: String, // e.g. "October 2026"
    val timestamp: Long = System.currentTimeMillis()
)
