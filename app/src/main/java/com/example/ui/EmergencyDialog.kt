package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyDialog(
    vanId: String,
    vanNumber: String,
    onDismiss: () -> Unit,
    onSubmit: (reason: String, location: String, requestReserveReplacement: Boolean) -> Unit
) {
    val predefinedReasons = listOf(
        "Brake Inspection / Pressure Anomaly",
        "Flat Tire / Puncture Replacement",
        "Engine Overheating Warning",
        "Coolant or Fluid Leak",
        "Electrical / Door Sensor Failure",
        "Road Obstruction / Traffic Collision"
    )

    var selectedReason by remember { mutableStateOf(predefinedReasons[0]) }
    var customReason by remember { mutableStateOf("") }
    var locationDescription by remember { mutableStateOf("Near Financial District & Market St") }
    var requestReserveVan by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("emergency_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Emergency & Van Replacement", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "Vehicle: $vanId ($vanNumber)",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB71C1C)
                        )
                        Text(
                            "Logging this emergency will alert Operations Dispatch, update commuter ETAs, and dispatch a replacement van.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Incident Reason:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                predefinedReasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customReason,
                    onValueChange = { customReason = it },
                    label = { Text("Additional Technical Notes (Optional)") },
                    placeholder = { Text("e.g. Warning chime triggered on dash") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_reason")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = locationDescription,
                    onValueChange = { locationDescription = it },
                    label = { Text("Current Location / Breakdown Spot") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_location")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Deploy Reserve Van (VAN-09R)",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Dispatches standby Mercedes Sprinter to resume route stops.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = requestReserveVan,
                            onCheckedChange = { requestReserveVan = it },
                            modifier = Modifier.testTag("toggle_reserve_van")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (customReason.isNotBlank()) "$selectedReason: $customReason" else selectedReason
                    onSubmit(finalReason, locationDescription, requestReserveVan)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                modifier = Modifier.testTag("confirm_emergency_btn")
            ) {
                Text("Confirm Incident")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_emergency_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
