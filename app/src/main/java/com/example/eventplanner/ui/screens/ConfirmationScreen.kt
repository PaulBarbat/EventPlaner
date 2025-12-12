package com.example.eventplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var distancePrice = 0

    Column(
        modifier = Modifier.padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(onClick = { viewModel.updateFormState(4) }) {
                Text("Back")
            }
            Button(onClick = {
                viewModel.saveBooking()
                viewModel.updateFormState(1)
            }) {
                Text("Confirm Booking")
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text("Data Evenimentului: $selectedDate", modifier = Modifier.padding(8.dp))
            Text("Numar de persoane: $selectedNumber", modifier = Modifier.padding(8.dp))
            Text("Ore: $selectedHours", modifier = Modifier.padding(8.dp))
            routeDistance?.let {
                val dist = (it/1000.0).toInt()
                Text(String.format("Distanta pana la eveniment: ${dist}"), modifier = Modifier.padding(8.dp))
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp,
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            if(services.isEmpty()){
                Text("Nu ati selectat servicii!", modifier = Modifier.padding(8.dp))
            }else {
                Text("Serviciile selectate", modifier = Modifier.padding(8.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(services) { service ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(service.first.displayName, modifier = Modifier.padding(8.dp))
                            Text("${service.second.displayName}", modifier = Modifier.padding(8.dp))
                            Text("${viewModel.calculateSelectedServicePrice(service.first)} Euro", modifier = Modifier.padding(8.dp))
                        }
                        Divider()
                    }
                    routeDistance?.let {
                        distancePrice = (it / 1000.0 * 2 * 0.6).toInt()
                        item{
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Transport", modifier = Modifier.padding(8.dp))
                                Text("${distancePrice} Euro", modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                    item{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            var total = distancePrice
                            services.forEach { service ->
                                total += viewModel.calculateSelectedServicePrice(service.first)
                            }
                            Text("Total", modifier = Modifier.padding(8.dp))
                            Text("${total} Euro", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}