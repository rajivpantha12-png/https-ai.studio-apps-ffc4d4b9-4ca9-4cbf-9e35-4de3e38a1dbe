package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "van_fleet")
data class VanFleetEntity(
    @PrimaryKey
    val vanId: String, // e.g. "VAN-04"
    val vanNumber: String, // e.g. "BA-2-KHA-4091"
    val model: String = "Toyota HiAce Commuter",
    val capacity: Int = 16,
    val currentPassengers: Int = 9,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val routeId: String,
    val currentStopId: String,
    val nextStopName: String,
    val speedKmh: Int = 38,
    val status: String = "IN_SERVICE", // "IN_SERVICE", "RESERVE", "EMERGENCY_MAINTENANCE", "REPLACED", "OFF_ROUTE"
    val replacementVanId: String? = null,
    val statusReason: String? = null,
    val lastReportedTime: Long = System.currentTimeMillis()
)
