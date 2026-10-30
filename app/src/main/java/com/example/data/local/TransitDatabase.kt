package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccountEntity::class,
        TransitRouteEntity::class,
        RouteStopEntity::class,
        VanFleetEntity::class,
        DepartureLogEntity::class,
        EmergencyEventEntity::class,
        DriverRatingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TransitDatabase : RoomDatabase() {

    abstract fun transitDao(): TransitDao

    companion object {
        @Volatile
        private var INSTANCE: TransitDatabase? = null

        fun getInstance(context: Context): TransitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransitDatabase::class.java,
                    "transit_flow.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialTransitData(database.transitDao())
                    }
                }
            }
        }

        private suspend fun populateInitialTransitData(dao: TransitDao) {
            // Initial Users
            val initialUsers = listOf(
                UserAccountEntity(
                    id = "USR-DRV-01",
                    email = "raj.shrestha@gmail.com",
                    fullName = "Rajesh Shrestha",
                    phoneNumber = "+1 (555) 234-5678",
                    role = "DRIVER",
                    officeId = "OFC-TRANSIT-01",
                    drivingLicenseNumber = "DL-CA-9874102-X",
                    isLicenseVerified = true,
                    licenseExpiryDate = "2029-08-14",
                    assignedVehicleNumber = "VAN-04",
                    ratingScore = 4.9,
                    ratingsCount = 28
                ),
                UserAccountEntity(
                    id = "USR-DRV-02",
                    email = "sunil.karki@gmail.com",
                    fullName = "Sunil Karki",
                    phoneNumber = "+1 (555) 876-5432",
                    role = "DRIVER",
                    officeId = "OFC-TRANSIT-01",
                    drivingLicenseNumber = "DL-CA-4421890-B",
                    isLicenseVerified = true,
                    licenseExpiryDate = "2028-05-20",
                    assignedVehicleNumber = "VAN-09R",
                    ratingScore = 4.7,
                    ratingsCount = 19
                ),
                UserAccountEntity(
                    id = "USR-PAX-01",
                    email = "sarah.jenkins@gmail.com",
                    fullName = "Sarah Jenkins",
                    phoneNumber = "+1 (555) 345-6789",
                    role = "PASSENGER",
                    passengerId = "PAX-8921",
                    ratingScore = 5.0,
                    ratingsCount = 0
                ),
                UserAccountEntity(
                    id = "USR-PAX-02",
                    email = "michael.chen@gmail.com",
                    fullName = "Michael Chen",
                    phoneNumber = "+1 (555) 901-2345",
                    role = "PASSENGER",
                    passengerId = "PAX-6340",
                    ratingScore = 5.0,
                    ratingsCount = 0
                ),
                UserAccountEntity(
                    id = "USR-ADM-01",
                    email = "dispatch@transitflow.com",
                    fullName = "Fleet Operations Dispatch",
                    phoneNumber = "+1 (555) 100-2000",
                    role = "FLEET_OWNER",
                    officeId = "OFC-HQ-MAIN"
                )
            )
            initialUsers.forEach { dao.insertUser(it) }

            // Initial Routes
            val routes = listOf(
                TransitRouteEntity(
                    routeId = "ROUTE-101",
                    routeCode = "R-101",
                    routeName = "Downtown - Airport Express",
                    origin = "Metro Central Terminal",
                    destination = "Terminal 3 SkyPort",
                    totalDistanceKm = 18.4,
                    typicalDurationMin = 36,
                    colorHex = "#0D47A1",
                    activeVanCount = 2
                ),
                TransitRouteEntity(
                    routeId = "ROUTE-202",
                    routeCode = "R-202",
                    routeName = "Eastside Campus - Tech Corridor",
                    origin = "University Plaza",
                    destination = "Innovation Silicon Campus",
                    totalDistanceKm = 12.8,
                    typicalDurationMin = 28,
                    colorHex = "#00695C",
                    activeVanCount = 1
                )
            )
            dao.insertRoutes(routes)

            // Initial Stops for Route 101
            val stopsRoute101 = listOf(
                RouteStopEntity(
                    stopId = "STOP-101-1",
                    routeId = "ROUTE-101",
                    stopSequence = 1,
                    stopName = "Metro Central Terminal (Bay 4)",
                    latitude = 37.7749,
                    longitude = -122.4194,
                    scheduledTime = "08:00 AM",
                    estimatedTime = "08:00 AM",
                    actualDepartureTime = "08:02 AM",
                    trafficDelayMin = 2,
                    status = "DEPARTED",
                    passengersWaiting = 0
                ),
                RouteStopEntity(
                    stopId = "STOP-101-2",
                    routeId = "ROUTE-101",
                    stopSequence = 2,
                    stopName = "Financial District Station",
                    latitude = 37.7915,
                    longitude = -122.4014,
                    scheduledTime = "08:12 AM",
                    estimatedTime = "08:15 AM",
                    actualDepartureTime = "08:16 AM",
                    trafficDelayMin = 4,
                    status = "DEPARTED",
                    passengersWaiting = 0
                ),
                RouteStopEntity(
                    stopId = "STOP-101-3",
                    routeId = "ROUTE-101",
                    stopSequence = 3,
                    stopName = "South Park Boulevard",
                    latitude = 37.7820,
                    longitude = -122.3940,
                    scheduledTime = "08:24 AM",
                    estimatedTime = "08:27 AM",
                    actualDepartureTime = null,
                    trafficDelayMin = 3,
                    status = "APPROACHING",
                    passengersWaiting = 6
                ),
                RouteStopEntity(
                    stopId = "STOP-101-4",
                    routeId = "ROUTE-101",
                    stopSequence = 4,
                    stopName = "Mission Bay Transit Center",
                    latitude = 37.7690,
                    longitude = -122.3910,
                    scheduledTime = "08:35 AM",
                    estimatedTime = "08:38 AM",
                    actualDepartureTime = null,
                    trafficDelayMin = 3,
                    status = "UPCOMING",
                    passengersWaiting = 4
                ),
                RouteStopEntity(
                    stopId = "STOP-101-5",
                    routeId = "ROUTE-101",
                    stopSequence = 5,
                    stopName = "Airport Terminal 3 SkyPort",
                    latitude = 37.6213,
                    longitude = -122.3790,
                    scheduledTime = "08:52 AM",
                    estimatedTime = "08:55 AM",
                    actualDepartureTime = null,
                    trafficDelayMin = 3,
                    status = "UPCOMING",
                    passengersWaiting = 2
                )
            )
            dao.insertStops(stopsRoute101)

            // Initial Fleet Vans
            val vans = listOf(
                VanFleetEntity(
                    vanId = "VAN-04",
                    vanNumber = "TF-4091-EXP",
                    model = "Toyota HiAce Super GL",
                    capacity = 16,
                    currentPassengers = 9,
                    driverId = "USR-DRV-01",
                    driverName = "Rajesh Shrestha",
                    driverPhone = "+1 (555) 234-5678",
                    routeId = "ROUTE-101",
                    currentStopId = "STOP-101-3",
                    nextStopName = "South Park Boulevard",
                    speedKmh = 42,
                    status = "IN_SERVICE",
                    statusReason = "On scheduled run",
                    lastReportedTime = System.currentTimeMillis()
                ),
                VanFleetEntity(
                    vanId = "VAN-09R",
                    vanNumber = "TF-9012-RES",
                    model = "Mercedes Sprinter 516",
                    capacity = 18,
                    currentPassengers = 0,
                    driverId = "USR-DRV-02",
                    driverName = "Sunil Karki",
                    driverPhone = "+1 (555) 876-5432",
                    routeId = "ROUTE-101",
                    currentStopId = "STOP-101-1",
                    nextStopName = "Metro Central Terminal",
                    speedKmh = 0,
                    status = "RESERVE",
                    statusReason = "Standby reserve van ready for dispatch",
                    lastReportedTime = System.currentTimeMillis()
                )
            )
            dao.insertVans(vans)

            // Initial Departure Logs
            val initialLogs = listOf(
                DepartureLogEntity(
                    logId = "LOG-1001",
                    vanId = "VAN-04",
                    driverId = "USR-DRV-01",
                    stopId = "STOP-101-1",
                    stopName = "Metro Central Terminal (Bay 4)",
                    scheduledDeparture = "08:00 AM",
                    actualDeparture = "08:02 AM",
                    delayMinutes = 2,
                    passengersBoarded = 7,
                    passengersAlighted = 0,
                    notes = "On-time departure with morning commuter load"
                ),
                DepartureLogEntity(
                    logId = "LOG-1002",
                    vanId = "VAN-04",
                    driverId = "USR-DRV-01",
                    stopId = "STOP-101-2",
                    stopName = "Financial District Station",
                    scheduledDeparture = "08:12 AM",
                    actualDeparture = "08:16 AM",
                    delayMinutes = 4,
                    passengersBoarded = 4,
                    passengersAlighted = 2,
                    notes = "Signal delay near Market St intersection"
                )
            )
            initialLogs.forEach { dao.insertDepartureLog(it) }

            // Initial Driver Ratings
            val ratings = listOf(
                DriverRatingEntity(
                    ratingId = "RAT-101",
                    driverId = "USR-DRV-01",
                    driverName = "Rajesh Shrestha",
                    passengerId = "PAX-8921",
                    passengerName = "Sarah Jenkins",
                    stars = 5,
                    punctualityScore = 5,
                    safetyScore = 5,
                    serviceScore = 5,
                    feedbackText = "Very smooth driving, announced upcoming stops clearly!",
                    monthYear = "October 2026"
                ),
                DriverRatingEntity(
                    ratingId = "RAT-102",
                    driverId = "USR-DRV-01",
                    driverName = "Rajesh Shrestha",
                    passengerId = "PAX-6340",
                    passengerName = "Michael Chen",
                    stars = 5,
                    punctualityScore = 5,
                    safetyScore = 5,
                    serviceScore = 4,
                    feedbackText = "Safe driver and arrived within 2 minutes of the calculated ETA.",
                    monthYear = "October 2026"
                )
            )
            ratings.forEach { dao.insertRating(it) }
        }
    }
}
