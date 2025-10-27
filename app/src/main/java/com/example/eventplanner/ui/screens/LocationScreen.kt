package com.example.eventplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventplanner.BuildConfig
import com.example.eventplanner.ui.elements.MapRoutePickerElement
import com.example.eventplanner.viewmodel.EventDateViewModel


//SecondForm
@Composable
fun LocationScreen(viewModel: EventDateViewModel)
{
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { viewModel.updateFormState(1) }) {
            Text("Back")
        }
        MapRoutePickerElement(viewModel, apiKey = BuildConfig.ORS_KEY)
    }
}