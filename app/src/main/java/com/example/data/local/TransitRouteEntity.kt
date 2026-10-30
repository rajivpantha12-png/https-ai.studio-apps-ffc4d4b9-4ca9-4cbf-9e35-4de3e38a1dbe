package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transit_routes")
data class TransitRouteEntity(
    @PrimaryKey
    val routeId: String, // e.g. "ROUTE-101"
    val routeCode: String, // e.g. "R-101"
    val routeName: String, // e.g. "Metro Express Line A"
    val origin: String, // e.g. "Central Transit Terminal"
    val destination: String, // e.g. "North Tech Park"
    val totalDistanceKm: Double,
    val typicalDurationMin: Int,
    val colorHex: String = "#0D47A1",
    val activeVanCount: Int = 2
)
