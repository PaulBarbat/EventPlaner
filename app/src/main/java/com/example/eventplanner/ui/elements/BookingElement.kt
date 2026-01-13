package com.example.eventplanner.ui.elements

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@SuppressLint("DefaultLocale")
@Composable
fun BookingItem(
    booking: com.example.eventplanner.data.models.Booking,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Date: ${booking.date}")
            Text("People: ${booking.numberOfPeople}")
            Text("Hours: ${booking.hours}")
            booking.routeDistance?.let {
                Text(String.format("Distance: %.1f km", it / 1000))
            }
            if (booking.selectedServices.isNotEmpty()) {
                Text("Services:")
                booking.selectedServices.forEach { (service, tuk) ->
                    Text(" - $service on $tuk")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        }
    }
}