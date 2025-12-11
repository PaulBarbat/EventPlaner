package com.example.eventplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.eventplanner.viewmodel.EventDateViewModel


@Composable
fun ConfirmationScreen(viewModel: EventDateViewModel)
{
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedNumber by viewModel.selectedNumber.collectAsState()
    val selectedHours by viewModel.selectedHours.collectAsState()
    val routeDistance by viewModel.distance.collectAsState()
    val context = LocalContext.current
    val services = viewModel.selectedServices

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { viewModel.updateFormState(2) }) {
            Text("Back")
        }

        Text("Data Evenimentului: $selectedDate", modifier = Modifier.padding(4.dp))
        Text("Numar de persoane: $selectedNumber", modifier = Modifier.padding(4.dp))
        Text("Ore: $selectedHours", modifier = Modifier.padding(4.dp))
        routeDistance?.let {
            Text(
                String.format("Distanta pana la eveniment:", it / 1000.0),
                modifier = Modifier.padding(14.dp)
            )
        }
        if(services.isEmpty()){
            Text("Nu ati selectat servicii!", modifier = Modifier.padding(16.dp))
        }else{
            Text("Serviciile selectate", modifier = Modifier.padding(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(services){ service->
                    Row(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement =  Arrangement.SpaceBetween
                    ) {
                        Text(service.first.displayName)
                        Text("${service.second.displayName}")
                        Text("pret ${viewModel.calculateSelectedServicePrice(service.first)}")
                    }
                    Divider()
                }
            }
            Button(onClick = {
                viewModel.saveBooking()
                viewModel.updateFormState(0)
            }) {
                Text("Confirm Booking")
            }
        }
    }
}