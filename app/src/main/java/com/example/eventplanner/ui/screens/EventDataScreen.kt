package com.example.eventplanner.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eventplanner.ui.elements.NumberDropdownElement
import com.example.eventplanner.viewmodel.EventDateViewModel


@Composable
fun EventDataScreen(viewModel: EventDateViewModel)
{
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        if(selectedDate!=null){
            Text("Data Evenimentului: $selectedDate")
        }
        Text("Numar de persoane: $selectedNumber")
        Text("Ore: $selectedHours")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.updateFormState(1)},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp)
            ) {
                Text("Back")
            }
            Button(
                onClick = { viewModel.updateFormState(3)},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp)
            ) {
                Text("Continue")
            }
        }
    }
}