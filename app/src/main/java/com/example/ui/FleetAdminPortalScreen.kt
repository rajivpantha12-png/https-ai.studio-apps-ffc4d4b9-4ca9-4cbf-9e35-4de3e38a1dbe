package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.EmergencyEventEntity
import com.example.data.local.UserAccountEntity
import com.example.data.local.VanFleetEntity
import com.example.domain.ai.AiIncidentAuditResult
import com.example.domain.ai.AiOptimizationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetAdminPortalScreen(
    vans: List<VanFleetEntity>,
    drivers: List<UserAccountEntity>,
    passengers: List<UserAccountEntity>,
    emergencyEvents: List<EmergencyEventEntity>,
    aiOptimization: AiOptimizationResult?,
    aiAuditResult: AiIncidentAuditResult?,
    isAiLoading: Boolean,
    onRunAiOptimization: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Fleet Status", "Drivers & License", "Passenger IDs", "AI Copilot")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("fleet_admin_portal_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Operations KPI Dashboard ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "TransitFlow Operations Center",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Company Office ID: OFC-HQ-MAIN • Fleet Management",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiItem(
                            title = "Active Vans",
                            value = "${vans.count { it.status == "IN_SERVICE" }}",
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                        KpiItem(
                            title = "Reserve Vans",
                            value = "${vans.count { it.status == "RESERVE" }}",
                            color = Color(0xFF1565C0),
                            modifier = Modifier.weight(1f)
                        )
                        KpiItem(
                            title = "Maintenance",
                            value = "${vans.count { it.status == "EMERGENCY_MAINTENANCE" || it.status == "REPLACED" }}",
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        KpiItem(
                            title = "Passengers",
                            value = "${passengers.size}",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- Admin Navigation Tabs ---
        item {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }
        }

        // --- TAB 0: Fleet Status & Emergency Logs ---
        if (selectedTab == 0) {
            item {
                Text("Fleet Vehicles & Live Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(vans) { van ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "${van.vanId} • ${van.vanNumber}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "${van.model} • Driver: ${van.driverName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = when (van.status) {
                                    "IN_SERVICE" -> Color(0xFFE8F5E9)
                                    "RESERVE" -> Color(0xFFE3F2FD)
                                    else -> Color(0xFFFFEBEE)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    van.status.replace("_", " "),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when (van.status) {
                                        "IN_SERVICE" -> Color(0xFF2E7D32)
                                        "RESERVE" -> Color(0xFF1565C0)
                                        else -> Color(0xFFC62828)
                                    }
                                )
                            }
                        }

                        if (van.statusReason != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Log: ${van.statusReason}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            if (emergencyEvents.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Emergency Incidents & Replacements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(emergencyEvents) { event ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Incident: ${event.eventType.replace("_", " ")}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB71C1C)
                                )
                                Text(
                                    event.eventId,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFB71C1C)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Vehicle: ${event.vanNumber} • Location: ${event.locationDescription}", style = MaterialTheme.typography.bodySmall)
                            Text("Reason: ${event.reason}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            if (event.assignedReplacementVan != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Replacement Dispatched: ${event.assignedReplacementVan}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- TAB 1: Drivers & License Verification ---
        if (selectedTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verified Driver Registry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Office: OFC-TRANSIT-01", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            items(drivers) { driver ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(driver.fullName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(driver.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified License",
                                            tint = Color(0xFF1976D2),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(driver.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Rating
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${driver.ratingScore}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Driving License", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    driver.drivingLicenseNumber ?: "Pending Submission",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("License Expiry", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    driver.licenseExpiryDate ?: "2029-12-31",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- TAB 2: Passenger Accounts & Passenger IDs ---
        if (selectedTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Passenger Accounts Directory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${passengers.size} Commuters", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            items(passengers) { passenger ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(passenger.fullName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(passenger.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(passenger.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(passenger.phoneNumber, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        // Unique Passenger ID Badge
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                passenger.passengerId ?: "PAX-1001",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // --- TAB 3: AI Fleet Operations & Security Copilot ---
        if (selectedTab == 3) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Route Optimizer & Fleet Security", fontWeight = FontWeight.Bold)
                            }
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "AI engine continuously analyzes GPS telemetry, signal delays, passenger loading, and mechanical logs to recommend cadence adjustments.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRunAiOptimization,
                            enabled = !isAiLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_ai_optimizer_btn")
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run AI Operations Audit")
                        }
                    }
                }
            }

            if (aiOptimization != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("AI Route Analysis", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        aiOptimization.routeHealthStatus,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(aiOptimization.trafficAnalysis, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Recommended Speed: ${aiOptimization.recommendedSpeedKmh} km/h • Predicted Destination Delay: ${aiOptimization.predictedDelayAtDestinationMin} min",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Suggested Dispatch Action:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    Text(aiOptimization.suggestedAction, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Security Note: ${aiOptimization.securityNotice}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            if (aiAuditResult != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("AI Emergency Incident Audit", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                Surface(
                                    color = Color(0xFFFFCCBC),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Severity: ${aiAuditResult.incidentSeverity}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBF360C)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(aiAuditResult.rootCauseSummary, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(aiAuditResult.fleetImpactAssessment, style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Preventive Maintenance: ${aiAuditResult.preventiveRecommendation}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD84315)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiItem(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
