package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class TrafficLevel(val label: String, val delayBonusMin: Int, val averageSpeedKmh: Int) {
    SMOOTH("Smooth Traffic", 0, 48),
    MODERATE("Moderate Congestion", 3, 34),
    HEAVY("Heavy Gridlock", 8, 18)
}

class TransitRepository(private val dao: TransitDao) {

    val allUsers: Flow<List<UserAccountEntity>> = dao.getAllUsers()
    val passengers: Flow<List<UserAccountEntity>> = dao.getUsersByRole("PASSENGER")
    val drivers: Flow<List<UserAccountEntity>> = dao.getUsersByRole("DRIVER")
    val allRoutes: Flow<List<TransitRouteEntity>> = dao.getAllRoutes()
    val allVans: Flow<List<VanFleetEntity>> = dao.getAllVans()
    val allDepartureLogs: Flow<List<DepartureLogEntity>> = dao.getAllDepartureLogs()
    val allEmergencyEvents: Flow<List<EmergencyEventEntity>> = dao.getAllEmergencyEvents()
    val allRatings: Flow<List<DriverRatingEntity>> = dao.getAllRatings()

    fun getStopsForRoute(routeId: String): Flow<List<RouteStopEntity>> {
        return dao.getStopsForRoute(routeId)
    }

    suspend fun getUserById(userId: String): UserAccountEntity? {
        return dao.getUserById(userId)
    }

    suspend fun registerOrUpdateUser(
        id: String = UUID.randomUUID().toString().take(8),
        email: String,
        fullName: String,
        phoneNumber: String,
        role: String,
        drivingLicenseNumber: String? = null,
        licenseExpiryDate: String? = null,
        officeId: String? = null
    ): UserAccountEntity {
        val passengerId = if (role == "PASSENGER") "PAX-${(1000..9999).random()}" else null
        val user = UserAccountEntity(
            id = id,
            email = email,
            fullName = fullName,
            phoneNumber = phoneNumber,
            role = role,
            passengerId = passengerId,
            officeId = officeId ?: if (role == "FLEET_OWNER" || role == "DRIVER") "OFC-TRANSIT-01" else null,
            drivingLicenseNumber = drivingLicenseNumber,
            isLicenseVerified = drivingLicenseNumber?.isNotBlank() == true,
            licenseExpiryDate = licenseExpiryDate ?: "2028-12-31",
            assignedVehicleNumber = if (role == "DRIVER") "VAN-04" else null,
            ratingScore = 5.0,
            ratingsCount = 0
        )
        dao.insertUser(user)
        return user
    }

    suspend fun recordVanLeftStop(
        vanId: String,
        stopId: String,
        scheduledDeparture: String,
        actualDeparture: String,
        delayMinutes: Int,
        passengersBoarded: Int,
        passengersAlighted: Int,
        notes: String
    ) {
        val stop = dao.getStopById(stopId) ?: return
        val van = dao.getVanById(vanId) ?: return

        // Update Stop state to DEPARTED with recorded actual departure time
        val updatedStop = stop.copy(
            status = "DEPARTED",
            actualDepartureTime = actualDeparture,
            trafficDelayMin = delayMinutes
        )
        dao.updateStop(updatedStop)

        // Insert Departure Log
        val log = DepartureLogEntity(
            logId = "LOG-${System.currentTimeMillis() % 100000}",
            vanId = vanId,
            driverId = van.driverId,
            stopId = stopId,
            stopName = stop.stopName,
            scheduledDeparture = scheduledDeparture,
            actualDeparture = actualDeparture,
            delayMinutes = delayMinutes,
            passengersBoarded = passengersBoarded,
            passengersAlighted = passengersAlighted,
            notes = notes
        )
        dao.insertDepartureLog(log)

        // Advance van passenger count and update next stop
        val newPassengerCount = (van.currentPassengers + passengersBoarded - passengersAlighted).coerceIn(0, van.capacity)
        dao.updateVan(
            van.copy(
                currentPassengers = newPassengerCount,
                lastReportedTime = System.currentTimeMillis()
            )
        )
    }

    suspend fun reportEmergencyMaintenance(
        vanId: String,
        reason: String,
        location: String,
        assignReserveVan: Boolean
    ): String {
        val van = dao.getVanById(vanId) ?: return "Vehicle not found"
        val eventId = "EMG-${System.currentTimeMillis() % 100000}"

        var replacementVanId: String? = null
        if (assignReserveVan) {
            // Find standby reserve van
            replacementVanId = "VAN-09R"
            val reserveVan = dao.getVanById(replacementVanId)
            if (reserveVan != null) {
                dao.updateVan(
                    reserveVan.copy(
                        status = "IN_SERVICE",
                        routeId = van.routeId,
                        currentStopId = van.currentStopId,
                        nextStopName = van.nextStopName,
                        statusReason = "Dispatched as replacement for ${van.vanNumber}"
                    )
                )
            }
        }

        // Mark troubled van
        dao.updateVan(
            van.copy(
                status = if (assignReserveVan) "REPLACED" else "EMERGENCY_MAINTENANCE",
                statusReason = reason,
                replacementVanId = replacementVanId,
                speedKmh = 0
            )
        )

        // Create emergency record
        val emergencyEvent = EmergencyEventEntity(
            eventId = eventId,
            vanId = vanId,
            vanNumber = van.vanNumber,
            driverId = van.driverId,
            eventType = if (assignReserveVan) "VEHICLE_REPLACEMENT" else "EMERGENCY_MAINTENANCE",
            reason = reason,
            assignedReplacementVan = replacementVanId,
            locationDescription = location,
            isResolved = false
        )
        dao.insertEmergencyEvent(emergencyEvent)

        return eventId
    }

    suspend fun resolveEmergencyEvent(eventId: String, vanId: String) {
        val van = dao.getVanById(vanId)
        if (van != null) {
            dao.updateVan(
                van.copy(
                    status = "IN_SERVICE",
                    statusReason = "Maintenance resolved. Back on route."
                )
            )
        }
    }

    suspend fun submitRating(
        driverId: String,
        driverName: String,
        passengerId: String,
        passengerName: String,
        stars: Int,
        punctualityScore: Int,
        safetyScore: Int,
        serviceScore: Int,
        feedbackText: String,
        monthYear: String = "October 2026"
    ) {
        val rating = DriverRatingEntity(
            ratingId = "RAT-${System.currentTimeMillis() % 100000}",
            driverId = driverId,
            driverName = driverName,
            passengerId = passengerId,
            passengerName = passengerName,
            stars = stars,
            punctualityScore = punctualityScore,
            safetyScore = safetyScore,
            serviceScore = serviceScore,
            feedbackText = feedbackText,
            monthYear = monthYear
        )
        dao.insertRating(rating)

        // Update driver's aggregate rating
        val driver = dao.getUserById(driverId)
        if (driver != null) {
            val newCount = driver.ratingsCount + 1
            val newScore = ((driver.ratingScore * driver.ratingsCount) + stars) / newCount
            dao.updateUser(
                driver.copy(
                    ratingsCount = newCount,
                    ratingScore = String.format(Locale.US, "%.1f", newScore).toDouble()
                )
            )
        }
    }

    suspend fun adjustRouteTrafficCondition(routeId: String, trafficLevel: TrafficLevel) {
        // Adjust vehicle speed and recalculate ETAs for upcoming stops
        val vans = dao.getVanById("VAN-04")
        if (vans != null) {
            dao.updateVan(vans.copy(speedKmh = trafficLevel.averageSpeedKmh))
        }
    }
}
