package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
fun DriverRatingDialog(
    driverName: String,
    driverId: String,
    onDismiss: () -> Unit,
    onSubmit: (stars: Int, punctuality: Int, safety: Int, service: Int, feedback: String) -> Unit
) {
    var overallStars by remember { mutableIntStateOf(5) }
    var punctualityScore by remember { mutableIntStateOf(5) }
    var safetyScore by remember { mutableIntStateOf(5) }
    var serviceScore by remember { mutableIntStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("driver_rating_dialog"),
        title = {
            Column {
                Text("Rate Driver - Monthly Review", fontWeight = FontWeight.Bold)
                Text(
                    "Driver: $driverName (October 2026)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Your monthly review helps maintain driver safety, timing accuracy, and company standards.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Overall Rating:", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { starIndex ->
                        Icon(
                            imageVector = if (starIndex <= overallStars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $starIndex",
                            tint = if (starIndex <= overallStars) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { overallStars = starIndex }
                                .padding(2.dp)
                                .testTag("star_$starIndex")
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$overallStars / 5",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                RatingCriteriaRow(
                    label = "Stop Timing & Punctuality",
                    score = punctualityScore,
                    onScoreChange = { punctualityScore = it }
                )

                RatingCriteriaRow(
                    label = "Safe Driving & Smooth Stops",
                    score = safetyScore,
                    onScoreChange = { safetyScore = it }
                )

                RatingCriteriaRow(
                    label = "Courtesy & Passenger Info",
                    score = serviceScore,
                    onScoreChange = { serviceScore = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Commuter Feedback & Comments") },
                    placeholder = { Text("e.g. Always punctual, announced delays clearly.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_feedback"),
                    minLines = 3,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        overallStars,
                        punctualityScore,
                        safetyScore,
                        serviceScore,
                        feedbackText.ifBlank { "Great driver, on-time departures." }
                    )
                },
                modifier = Modifier.testTag("submit_rating_btn")
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_rating_btn")
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun RatingCriteriaRow(
    label: String,
    score: Int,
    onScoreChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text("$score ★", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = score.toFloat(),
            onValueChange = { onScoreChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
