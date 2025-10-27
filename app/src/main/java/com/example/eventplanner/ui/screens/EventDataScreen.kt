package com.example.eventplanner.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eventplanner.ui.elements.NumberDropdownElement
import com.example.eventplanner.viewmodel.EventDateViewModel
import java.time.LocalDate
import java.util.Calendar


@Composable
fun EventDataScreen(viewModel: EventDateViewModel,
                    viewBookings:() -> Unit)
{
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()

    val calendar = Calendar.getInstance()

    val context = LocalContext.current
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
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier= Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically){

            Button(onClick = { datePickerDialog.show() }) {
                Text("Alege Data")
            }
            Button(onClick = { viewBookings() }) {
                Text("My Bookings")
            }
        }

        OutlinedTextField(
            value = selectedNumber.toString(),
            onValueChange = { input ->
                val number = input.toIntOrNull()
                if (number != null && number in 1..1000) {
                    viewModel.updateNumber(number)
                }
            },
            label = { Text("Numar de persoane") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        NumberDropdownElement(
            selectedHours = selectedHours,
            onNumberSelected = { viewModel.updateHours(it) },
            numberList = listOf(4, 6, 8)
        )

        Text("Data Evenimentului: $selectedDate")
        Text("Numar de persoane: $selectedNumber")
        Text("Ore: $selectedHours")

        Button(onClick = { viewModel.updateFormState(2) }) {
            Text("Continue")
        }
    }
}