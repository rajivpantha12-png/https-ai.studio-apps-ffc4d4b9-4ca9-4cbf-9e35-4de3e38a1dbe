package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_events")
data class EmergencyEventEntity(
    @PrimaryKey
    val eventId: String,
    val vanId: String,
    val vanNumber: String,
    val driverId: String,
    val eventType: String, // "EMERGENCY_MAINTENANCE", "VEHICLE_REPLACEMENT", "RESERVE_DISPATCH", "OFF_ROUTE"
    val reason: String,
    val assignedReplacementVan: String? = null,
    val locationDescription: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)
