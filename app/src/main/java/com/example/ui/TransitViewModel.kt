package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.TrafficLevel
import com.example.data.repository.TransitRepository
import com.example.domain.ai.AiIncidentAuditResult
import com.example.domain.ai.AiOptimizationResult
import com.example.domain.ai.TransitAiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class UserRole(val displayName: String, val badgeText: String) {
    PASSENGER("Passenger App", "Commuter Portal"),
    DRIVER("Driver Portal", "Driver Shift Log"),
    FLEET_OWNER("Operations Hub", "Admin / Owner Portal")
}

class TransitViewModel(
    private val repository: TransitRepository,
    private val aiService: TransitAiService = TransitAiService()
) : ViewModel() {

    private val _currentRole = MutableStateFlow(UserRole.PASSENGER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: StateFlow<UserAccountEntity?> = _currentUser.asStateFlow()

    val allRoutes: StateFlow<List<TransitRouteEntity>> = repository.allRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVans: StateFlow<List<VanFleetEntity>> = repository.allVans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val departureLogs: StateFlow<List<DepartureLogEntity>> = repository.allDepartureLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyEvents: StateFlow<List<EmergencyEventEntity>> = repository.allEmergencyEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val passengersList: StateFlow<List<UserAccountEntity>> = repository.passengers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val driversList: StateFlow<List<UserAccountEntity>> = repository.drivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val driverRatings: StateFlow<List<DriverRatingEntity>> = repository.allRatings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRouteId = MutableStateFlow("ROUTE-101")
    val selectedRouteId: StateFlow<String> = _selectedRouteId.asStateFlow()

    val currentRouteStops: StateFlow<List<RouteStopEntity>> = _selectedRouteId
        .flatMapLatest { routeId -> repository.getStopsForRoute(routeId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _trafficCondition = MutableStateFlow(TrafficLevel.MODERATE)
    val trafficCondition: StateFlow<TrafficLevel> = _trafficCondition.asStateFlow()

    private val _aiOptimization = MutableStateFlow<AiOptimizationResult?>(null)
    val aiOptimization: StateFlow<AiOptimizationResult?> = _aiOptimization.asStateFlow()

    private val _aiAuditResult = MutableStateFlow<AiIncidentAuditResult?>(null)
    val aiAuditResult: StateFlow<AiIncidentAuditResult?> = _aiAuditResult.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    init {
        // Set default passenger
        viewModelScope.launch {
            repository.passengers.firstOrNull()?.firstOrNull()?.let {
                _currentUser.value = it
            }
        }
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        viewModelScope.launch {
            when (role) {
                UserRole.PASSENGER -> {
                    _currentUser.value = repository.passengers.firstOrNull()?.firstOrNull()
                }
                UserRole.DRIVER -> {
                    _currentUser.value = repository.drivers.firstOrNull()?.firstOrNull()
                }
                UserRole.FLEET_OWNER -> {
                    _currentUser.value = repository.allUsers.firstOrNull()?.find { it.role == "FLEET_OWNER" }
                }
            }
        }
    }

    fun selectRoute(routeId: String) {
        _selectedRouteId.value = routeId
    }

    fun updateTraffic(level: TrafficLevel) {
        _trafficCondition.value = level
        viewModelScope.launch {
            repository.adjustRouteTrafficCondition(_selectedRouteId.value, level)
            _bannerMessage.value = "Traffic updated to ${level.label}. Stop ETAs recalculated."
        }
    }

    fun recordVanDeparture(
        vanId: String,
        stopId: String,
        scheduledTime: String,
        actualTime: String,
        delayMin: Int,
        boarded: Int,
        alighted: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.recordVanLeftStop(
                vanId = vanId,
                stopId = stopId,
                scheduledDeparture = scheduledTime,
                actualDeparture = actualTime,
                delayMinutes = delayMin,
                passengersBoarded = boarded,
                passengersAlighted = alighted,
                notes = notes
            )
            _bannerMessage.value = "Departure confirmed! Van left stop at $actualTime."
        }
    }

    fun reportEmergencyMaintenance(
        vanId: String,
        reason: String,
        location: String,
        requestReserveReplacement: Boolean
    ) {
        viewModelScope.launch {
            val eventId = repository.reportEmergencyMaintenance(
                vanId = vanId,
                reason = reason,
                location = location,
                assignReserveVan = requestReserveReplacement
            )
            _bannerMessage.value = if (requestReserveReplacement) {
                "Emergency reported ($eventId). Reserve van VAN-09R dispatched to take over route!"
            } else {
                "Emergency maintenance logged ($eventId). Vehicle marked out of service."
            }

            // Automatically trigger AI incident audit
            val van = allVans.value.find { it.vanId == vanId }
            _aiAuditResult.value = aiService.auditEmergencyMaintenance(
                vanNumber = van?.vanNumber ?: vanId,
                reason = reason,
                replacementAssigned = requestReserveReplacement
            )
        }
    }

    fun submitRating(
        driverId: String,
        driverName: String,
        stars: Int,
        punctuality: Int,
        safety: Int,
        service: Int,
        feedback: String
    ) {
        viewModelScope.launch {
            val passenger = _currentUser.value
            repository.submitRating(
                driverId = driverId,
                driverName = driverName,
                passengerId = passenger?.passengerId ?: "PAX-9000",
                passengerName = passenger?.fullName ?: "Commuter",
                stars = stars,
                punctualityScore = punctuality,
                safetyScore = safety,
                serviceScore = service,
                feedbackText = feedback
            )
            _bannerMessage.value = "Thank you! Driver rating submitted for October 2026."
        }
    }

    fun loginOrRegister(
        email: String,
        fullName: String,
        phone: String,
        role: String,
        licenseNumber: String? = null,
        licenseExpiry: String? = null,
        officeId: String? = null
    ) {
        viewModelScope.launch {
            val user = repository.registerOrUpdateUser(
                email = email,
                fullName = fullName,
                phoneNumber = phone,
                role = role,
                drivingLicenseNumber = licenseNumber,
                licenseExpiryDate = licenseExpiry,
                officeId = officeId
            )
            _currentUser.value = user
            _currentRole.value = when (role) {
                "DRIVER" -> UserRole.DRIVER
                "FLEET_OWNER" -> UserRole.FLEET_OWNER
                else -> UserRole.PASSENGER
            }
            _bannerMessage.value = "Welcome ${user.fullName}! Logged in as ${user.role}."
        }
    }

    fun runAiRouteOptimization() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val route = allRoutes.value.find { it.routeId == _selectedRouteId.value }
            val activeDelays = currentRouteStops.value.maxOfOrNull { it.trafficDelayMin } ?: 0
            val result = aiService.analyzeRouteAndTraffic(
                routeName = route?.routeName ?: "Metro Express Line A",
                currentTrafficLevel = _trafficCondition.value.label,
                vanCount = route?.activeVanCount ?: 2,
                activeDelaysMin = activeDelays
            )
            _aiOptimization.value = result
            _isAiLoading.value = false
            _bannerMessage.value = "AI Fleet Dispatch analysis completed."
        }
    }

    fun clearBanner() {
        _bannerMessage.value = null
    }

    class Factory(private val repository: TransitRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransitViewModel(repository) as T
        }
    }
}
