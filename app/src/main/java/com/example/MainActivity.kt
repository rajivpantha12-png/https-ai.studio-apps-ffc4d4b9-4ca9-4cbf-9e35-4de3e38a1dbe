package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.RouteStopEntity
import com.example.data.local.TransitDatabase
import com.example.data.repository.TransitRepository
import com.example.ui.*
import com.example.ui.theme.TransitFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TransitFlowTheme {
                val context = LocalContext.current
                val database = remember { TransitDatabase.getInstance(context) }
                val repository = remember { TransitRepository(database.transitDao()) }
                val viewModel: TransitViewModel = viewModel(
                    factory = TransitViewModel.Factory(repository)
                )

                TransitFlowApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransitFlowApp(viewModel: TransitViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val routes by viewModel.allRoutes.collectAsStateWithLifecycle()
    val selectedRouteId by viewModel.selectedRouteId.collectAsStateWithLifecycle()
    val stops by viewModel.currentRouteStops.collectAsStateWithLifecycle()
    val vans by viewModel.allVans.collectAsStateWithLifecycle()
    val departureLogs by viewModel.departureLogs.collectAsStateWithLifecycle()
    val emergencyEvents by viewModel.emergencyEvents.collectAsStateWithLifecycle()
    val passengers by viewModel.passengersList.collectAsStateWithLifecycle()
    val drivers by viewModel.driversList.collectAsStateWithLifecycle()
    val driverRatings by viewModel.driverRatings.collectAsStateWithLifecycle()
    val trafficCondition by viewModel.trafficCondition.collectAsStateWithLifecycle()
    val aiOptimization by viewModel.aiOptimization.collectAsStateWithLifecycle()
    val aiAuditResult by viewModel.aiAuditResult.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bannerMessage) {
        bannerMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearBanner()
        }
    }

    var showAuthModal by remember { mutableStateOf(false) }
    var showRatingModal by remember { mutableStateOf(false) }
    var showEmergencyModal by remember { mutableStateOf(false) }
    var stopForDepartureRecord by remember { mutableStateOf<RouteStopEntity?>(null) }

    val activeVan = vans.find { it.routeId == selectedRouteId } ?: vans.firstOrNull()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "TransitFlow",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            when (currentRole) {
                                UserRole.PASSENGER -> "Commuter Schedule & ETA"
                                UserRole.DRIVER -> "Driver Route Management"
                                UserRole.FLEET_OWNER -> "Transit Operations Hub"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                actions = {
                    // Profile / Switch Account button
                    FilledTonalButton(
                        onClick = { showAuthModal = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("switch_account_btn"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRole.PASSENGER -> Icons.Default.Person
                                UserRole.DRIVER -> Icons.Default.Badge
                                UserRole.FLEET_OWNER -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            currentUser?.fullName?.split(" ")?.firstOrNull() ?: currentRole.displayName,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentRole == UserRole.PASSENGER,
                    onClick = { viewModel.switchRole(UserRole.PASSENGER) },
                    icon = {
                        Icon(
                            if (currentRole == UserRole.PASSENGER) Icons.Filled.DirectionsWalk else Icons.Outlined.DirectionsWalk,
                            contentDescription = "Passenger"
                        )
                    },
                    label = { Text("Passenger") },
                    modifier = Modifier.testTag("nav_passenger")
                )
                NavigationBarItem(
                    selected = currentRole == UserRole.DRIVER,
                    onClick = { viewModel.switchRole(UserRole.DRIVER) },
                    icon = {
                        Icon(
                            if (currentRole == UserRole.DRIVER) Icons.Filled.DirectionsBus else Icons.Outlined.DirectionsBus,
                            contentDescription = "Driver"
                        )
                    },
                    label = { Text("Driver Log") },
                    modifier = Modifier.testTag("nav_driver")
                )
                NavigationBarItem(
                    selected = currentRole == UserRole.FLEET_OWNER,
                    onClick = { viewModel.switchRole(UserRole.FLEET_OWNER) },
                    icon = {
                        Icon(
                            if (currentRole == UserRole.FLEET_OWNER) Icons.Filled.BusinessCenter else Icons.Outlined.BusinessCenter,
                            contentDescription = "Fleet Operations"
                        )
                    },
                    label = { Text("Fleet Ops") },
                    modifier = Modifier.testTag("nav_fleet_ops")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRole) {
                UserRole.PASSENGER -> {
                    PassengerPortalScreen(
                        routes = routes,
                        selectedRouteId = selectedRouteId,
                        onSelectRoute = { viewModel.selectRoute(it) },
                        stops = stops,
                        vans = vans,
                        trafficCondition = trafficCondition,
                        onTrafficChange = { viewModel.updateTraffic(it) },
                        onRateDriverClick = { showRatingModal = true }
                    )
                }
                UserRole.DRIVER -> {
                    DriverPortalScreen(
                        driver = currentUser,
                        assignedVan = activeVan,
                        stops = stops,
                        departureLogs = departureLogs,
                        driverRatings = driverRatings,
                        onRecordStopDepartureClick = { stop ->
                            stopForDepartureRecord = stop
                        },
                        onEmergencyReportClick = { showEmergencyModal = true }
                    )
                }
                UserRole.FLEET_OWNER -> {
                    FleetAdminPortalScreen(
                        vans = vans,
                        drivers = drivers,
                        passengers = passengers,
                        emergencyEvents = emergencyEvents,
                        aiOptimization = aiOptimization,
                        aiAuditResult = aiAuditResult,
                        isAiLoading = isAiLoading,
                        onRunAiOptimization = { viewModel.runAiRouteOptimization() }
                    )
                }
            }
        }
    }

    // --- Auth / Profile Switch Modal ---
    if (showAuthModal) {
        AuthDialog(
            initialRole = when (currentRole) {
                UserRole.PASSENGER -> "PASSENGER"
                UserRole.DRIVER -> "DRIVER"
                UserRole.FLEET_OWNER -> "FLEET_OWNER"
            },
            onDismiss = { showAuthModal = false },
            onLoginSuccess = { email, name, phone, role, license, expiry, officeId ->
                viewModel.loginOrRegister(email, name, phone, role, license, expiry, officeId)
                showAuthModal = false
            }
        )
    }

    // --- Driver Monthly Rating Modal ---
    if (showRatingModal) {
        val targetDriver = activeVan?.driverName ?: "Rajesh Shrestha"
        val targetDriverId = activeVan?.driverId ?: "USR-DRV-01"
        DriverRatingDialog(
            driverName = targetDriver,
            driverId = targetDriverId,
            onDismiss = { showRatingModal = false },
            onSubmit = { stars, punctuality, safety, service, feedback ->
                viewModel.submitRating(targetDriverId, targetDriver, stars, punctuality, safety, service, feedback)
                showRatingModal = false
            }
        )
    }

    // --- Emergency & Vehicle Replacement Modal ---
    if (showEmergencyModal) {
        EmergencyDialog(
            vanId = activeVan?.vanId ?: "VAN-04",
            vanNumber = activeVan?.vanNumber ?: "BA-2-KHA-4091",
            onDismiss = { showEmergencyModal = false },
            onSubmit = { reason, location, requestReserve ->
                viewModel.reportEmergencyMaintenance(
                    vanId = activeVan?.vanId ?: "VAN-04",
                    reason = reason,
                    location = location,
                    requestReserveReplacement = requestReserve
                )
                showEmergencyModal = false
            }
        )
    }

    // --- Log Stop Departure Modal ---
    stopForDepartureRecord?.let { stop ->
        DepartureRecordDialog(
            stopName = stop.stopName,
            stopId = stop.stopId,
            scheduledTime = stop.scheduledTime,
            vanId = activeVan?.vanId ?: "VAN-04",
            onDismiss = { stopForDepartureRecord = null },
            onConfirmDeparture = { actualTime, delayMin, boarded, alighted, notes ->
                viewModel.recordVanDeparture(
                    vanId = activeVan?.vanId ?: "VAN-04",
                    stopId = stop.stopId,
                    scheduledTime = stop.scheduledTime,
                    actualTime = actualTime,
                    delayMin = delayMin,
                    boarded = boarded,
                    alighted = alighted,
                    notes = notes
                )
                stopForDepartureRecord = null
            }
        )
    }
}
