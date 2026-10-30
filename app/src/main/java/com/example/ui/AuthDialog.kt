package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    initialRole: String = "PASSENGER",
    onDismiss: () -> Unit,
    onLoginSuccess: (
        email: String,
        name: String,
        phone: String,
        role: String,
        licenseNumber: String?,
        licenseExpiry: String?,
        officeId: String?
    ) -> Unit
) {
    var selectedRole by remember { mutableStateOf(initialRole) }
    var email by remember { mutableStateOf(if (initialRole == "DRIVER") "driver.rajiv@gmail.com" else "passenger.commuter@gmail.com") }
    var fullName by remember { mutableStateOf(if (initialRole == "DRIVER") "Rajiv Transit Driver" else "Alex Commuter") }
    var phone by remember { mutableStateOf("+1 (555) 492-8812") }
    var licenseNumber by remember { mutableStateOf("DL-TX-9842109-A") }
    var licenseExpiry by remember { mutableStateOf("2029-06-30") }
    var officeId by remember { mutableStateOf("OFC-TRANSIT-01") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("auth_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("TransitFlow Sign-In", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Select Account Type:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedRole == "PASSENGER",
                        onClick = {
                            selectedRole = "PASSENGER"
                            email = "alex.passenger@gmail.com"
                            fullName = "Alex Commuter"
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                        modifier = Modifier.testTag("role_passenger_btn")
                    ) {
                        Text("Passenger", maxLines = 1)
                    }
                    SegmentedButton(
                        selected = selectedRole == "DRIVER",
                        onClick = {
                            selectedRole = "DRIVER"
                            email = "rajiv.driver@gmail.com"
                            fullName = "Rajiv Driver"
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                        modifier = Modifier.testTag("role_driver_btn")
                    ) {
                        Text("Driver", maxLines = 1)
                    }
                    SegmentedButton(
                        selected = selectedRole == "FLEET_OWNER",
                        onClick = {
                            selectedRole = "FLEET_OWNER"
                            email = "admin.dispatch@transitflow.com"
                            fullName = "Operations Admin"
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                        modifier = Modifier.testTag("role_admin_btn")
                    ) {
                        Text("Owner", maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_full_name"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Gmail / Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_phone"),
                    singleLine = true
                )

                if (selectedRole == "DRIVER") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Driver License Verification",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Text(
                                "Genuine driving license is required for regulatory compliance & Play Store verification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = licenseNumber,
                                onValueChange = { licenseNumber = it },
                                label = { Text("Driving License Number") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_license_number"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = licenseExpiry,
                                onValueChange = { licenseExpiry = it },
                                label = { Text("License Expiry Date (YYYY-MM-DD)") },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_license_expiry"),
                                singleLine = true
                            )
                        }
                    }
                }

                if (selectedRole == "FLEET_OWNER" || selectedRole == "DRIVER") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = officeId,
                        onValueChange = { officeId = it },
                        label = { Text("Company Office ID") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_office_id"),
                        singleLine = true
                    )
                }

                if (selectedRole == "PASSENGER") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Unique Passenger ID will be automatically generated upon login.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLoginSuccess(
                        email,
                        fullName,
                        phone,
                        selectedRole,
                        if (selectedRole == "DRIVER") licenseNumber else null,
                        if (selectedRole == "DRIVER") licenseExpiry else null,
                        if (selectedRole != "PASSENGER") officeId else null
                    )
                },
                modifier = Modifier.testTag("submit_auth_btn")
            ) {
                Text("Confirm & Sign In")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_auth_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
