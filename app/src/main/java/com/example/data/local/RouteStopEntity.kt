package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_stops")
data class RouteStopEntity(
    @PrimaryKey
    val stopId: String, // e.g. "STOP-101-1"
    val routeId: String,
    val stopSequence: Int,
    val stopName: String,
    val latitude: Double,
    val longitude: Double,
    val scheduledTime: String, // e.g. "08:15 AM"
    val estimatedTime: String, // dynamic ETA based on traffic GPS calculation e.g. "08:18 AM"
    val actualDepartureTime: String? = null, // recorded when the van leaves the stop e.g. "08:19 AM"
    val trafficDelayMin: Int = 0, // delay in minutes (+3 min, 0 min, etc)
    val status: String = "UPCOMING", // "UPCOMING", "APPROACHING", "ARRIVED", "DEPARTED", "SKIPPED"
    val passengersWaiting: Int = 4
)
