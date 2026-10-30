package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransitDao {

    // --- Users & Profiles ---
    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE role = :role ORDER BY fullName ASC")
    fun getUsersByRole(role: String): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    // --- Routes ---
    @Query("SELECT * FROM transit_routes ORDER BY routeCode ASC")
    fun getAllRoutes(): Flow<List<TransitRouteEntity>>

    @Query("SELECT * FROM transit_routes WHERE routeId = :routeId LIMIT 1")
    suspend fun getRouteById(routeId: String): TransitRouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<TransitRouteEntity>)

    // --- Route Stops ---
    @Query("SELECT * FROM route_stops WHERE routeId = :routeId ORDER BY stopSequence ASC")
    fun getStopsForRoute(routeId: String): Flow<List<RouteStopEntity>>

    @Query("SELECT * FROM route_stops ORDER BY stopSequence ASC")
    fun getAllStops(): Flow<List<RouteStopEntity>>

    @Query("SELECT * FROM route_stops WHERE stopId = :stopId LIMIT 1")
    suspend fun getStopById(stopId: String): RouteStopEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<RouteStopEntity>)

    @Update
    suspend fun updateStop(stop: RouteStopEntity)

    // --- Fleet Vans ---
    @Query("SELECT * FROM van_fleet ORDER BY vanId ASC")
    fun getAllVans(): Flow<List<VanFleetEntity>>

    @Query("SELECT * FROM van_fleet WHERE routeId = :routeId")
    fun getVansForRoute(routeId: String): Flow<List<VanFleetEntity>>

    @Query("SELECT * FROM van_fleet WHERE driverId = :driverId LIMIT 1")
    suspend fun getVanByDriverId(driverId: String): VanFleetEntity?

    @Query("SELECT * FROM van_fleet WHERE vanId = :vanId LIMIT 1")
    suspend fun getVanById(vanId: String): VanFleetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVans(vans: List<VanFleetEntity>)

    @Update
    suspend fun updateVan(van: VanFleetEntity)

    // --- Departure Logs ---
    @Query("SELECT * FROM departure_logs ORDER BY timestamp DESC")
    fun getAllDepartureLogs(): Flow<List<DepartureLogEntity>>

    @Query("SELECT * FROM departure_logs WHERE vanId = :vanId ORDER BY timestamp DESC")
    fun getLogsForVan(vanId: String): Flow<List<DepartureLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartureLog(log: DepartureLogEntity)

    // --- Emergency Events ---
    @Query("SELECT * FROM emergency_events ORDER BY timestamp DESC")
    fun getAllEmergencyEvents(): Flow<List<EmergencyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyEvent(event: EmergencyEventEntity)

    @Update
    suspend fun updateEmergencyEvent(event: EmergencyEventEntity)

    // --- Ratings ---
    @Query("SELECT * FROM driver_ratings WHERE driverId = :driverId ORDER BY timestamp DESC")
    fun getRatingsForDriver(driverId: String): Flow<List<DriverRatingEntity>>

    @Query("SELECT * FROM driver_ratings ORDER BY timestamp DESC")
    fun getAllRatings(): Flow<List<DriverRatingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: DriverRatingEntity)
}
