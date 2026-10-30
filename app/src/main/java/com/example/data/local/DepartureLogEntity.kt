package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departure_logs")
data class DepartureLogEntity(
    @PrimaryKey
    val logId: String, // e.g. "LOG-89412"
    val vanId: String,
    val driverId: String,
    val stopId: String,
    val stopName: String,
    val scheduledDeparture: String,
    val actualDeparture: String,
    val delayMinutes: Int,
    val passengersBoarded: Int,
    val passengersAlighted: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "Normal departure"
)
