package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureRecordDialog(
    stopName: String,
    stopId: String,
    scheduledTime: String,
    vanId: String,
    onDismiss: () -> Unit,
    onConfirmDeparture: (
        actualTime: String,
        delayMin: Int,
        boarded: Int,
        alighted: Int,
        notes: String
    ) -> Unit
) {
    val currentTimeStr = remember {
        SimpleDateFormat("hh:mm a", Locale.US).format(Date())
    }

    var actualTime by remember { mutableStateOf(currentTimeStr) }
    var delayMinutes by remember { mutableIntStateOf(2) }
    var boardedCount by remember { mutableIntStateOf(3) }
    var alightedCount by remember { mutableIntStateOf(1) }
    var departureNotes by remember { mutableStateOf("Departed smoothly, boarding complete") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("departure_record_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DepartureBoard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Van Left Stop", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(stopName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Scheduled Departure: $scheduledTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = actualTime,
                    onValueChange = { actualTime = it },
                    label = { Text("Actual Departure Time (When Van Left)") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_actual_time"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Traffic & Signal Delay (Minutes):", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0, 2, 4, 7, 10).forEach { mins ->
                        FilterChip(
                            selected = delayMinutes == mins,
                            onClick = { delayMinutes = mins },
                            label = { Text(if (mins == 0) "On Time" else "+$mins m") },
                            modifier = Modifier.testTag("delay_chip_$mins")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Boarded:", style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = { if (boardedCount > 0) boardedCount-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("-")
                            }
                            Text(
                                "$boardedCount",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { boardedCount++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("+")
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alighted:", style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = { if (alightedCount > 0) alightedCount-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("-")
                            }
                            Text(
                                "$alightedCount",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { alightedCount++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("+")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = departureNotes,
                    onValueChange = { departureNotes = it },
                    label = { Text("Route Log Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_departure_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDeparture(actualTime, delayMinutes, boardedCount, alightedCount, departureNotes)
                },
                modifier = Modifier.testTag("confirm_departure_btn")
            ) {
                Text("Confirm Van Left")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_departure_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
