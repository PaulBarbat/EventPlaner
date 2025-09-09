package com.example.eventplanner.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.eventplanner.viewmodel.EventDateViewModel
import java.time.LocalDate
import java.util.*

@Composable
fun EventDateScreen(viewModel: EventDateViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()

    val context = LocalContext.current

    // For date picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.updateDate(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Selected date: $selectedDate")
        Button(onClick = { datePickerDialog.show() }) {
            Text("Pick Date")
        }

        Text("Selected number: $selectedNumber")
        Slider(
            value = selectedNumber.toFloat(),
            onValueChange = { viewModel.updateNumber(it.toInt()) },
            valueRange = 1f..10f,
            steps = 8
        )
    }
}
